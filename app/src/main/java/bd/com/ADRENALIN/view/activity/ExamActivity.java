package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.Answer;
import bd.com.ADRENALIN.pojo.ApiGenericResponse;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.DraftSync;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.ExamDraft;
import bd.com.ADRENALIN.pojo.ExamSession;
import bd.com.ADRENALIN.pojo.Question;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.QuestionSerializer;
import bd.com.ADRENALIN.view.adapter.ExamRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Runs an exam (or a re-exam).
 *
 * Every answer is saved on the device at once and sent to the server a few seconds later, so
 * the student can continue after the app is closed, crashes or loses the connection. The
 * server clock decides how much time is left; once it runs out the server submits the saved
 * answers by itself and the attempt can no longer be continued.
 *
 * Created by mahfuz on 7/7/17.
 */
public class ExamActivity extends BaseActivity {

    public static final String TAG = ExamActivity.class.getSimpleName();
    private static final long SYNC_DEBOUNCE_MS = 4000;
    private static final long SYNC_HEARTBEAT_MS = 60000;
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btnSubmit)
    AppCompatButton btnSubmit;
    @BindView(R.id.tvExamTimer)
    TextView tvExamTimer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private int examId, durationInMinute;
    private String startAt;
    private boolean isExamRunning;
    private boolean isReExam;
    private boolean isSubmitting;
    private boolean isFinished;
    private Exam examInfo;
    private String userId;
    private ExamDraft draft;
    private int remainingSeconds;
    private CountDownTimer timer;
    private boolean dirty;
    private boolean syncInFlight;
    private final Handler syncHandler = new Handler(Looper.getMainLooper());
    private final Runnable syncRunnable = new Runnable() {
        @Override
        public void run() {
            syncDraft(false);
        }
    };
    private final Runnable heartbeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (isExamRunning && !isFinished) {
                syncDraft(true);
                syncHandler.postDelayed(this, SYNC_HEARTBEAT_MS);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        ButterKnife.bind(this);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = getPrefManager().getUserInfo() != null ? getPrefManager().getUserInfo().getId() : "";
        btnSubmit.setEnabled(false);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog).setMessage(R.string.lbl_exam_submit_confirmation)
                        .setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                submitExamAnswers(false);
                            }
                        })
                        .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String examString = intent.getStringExtra(AppConstants.ExamConstants.INTENT_CODE);
            isReExam = intent.getBooleanExtra(AppConstants.ExamConstants.INTENT_IS_REEXAM, false);
            if (examString != null && !examString.isEmpty()) {
                examInfo = new Gson().fromJson(examString, Exam.class);
                Log.e(TAG, examString);
                examId = examInfo.getId();
                durationInMinute = examInfo.getDuration();
                ExamActivity.this.setTitle(isReExam ? getString(R.string.lbl_reexam) + ": " + examInfo.getName() : examInfo.getName());
                isExamRunning = false;

                draft = getPrefManager().getExamDraft(userId, examId, isReExam);
                if (draft == null) {
                    draft = new ExamDraft();
                    draft.setStartedAtMillis(System.currentTimeMillis());
                    draft.setVersion(0);
                    draft.setReExam(isReExam);
                    draft.setExamJson(examString);
                    saveDraft();
                }
                startAt = AppUtils.getSlashSeparatedDateStringFromDate(new Date(draft.getStartedAtMillis()));
                startSession();
                return;
            }
        }
        showMsg(getString(R.string.lbl_no_data));
    }

    /* ------------------------------------------------------------------ session */

    private DraftSync buildSync() {
        return new DraftSync(userId, examId, isReExam, draft.getVersion(), draft.getElapsedSeconds(), draft.getAnswersJson());
    }

    /** Starts or resumes the attempt on the server, then loads the questions. */
    private void startSession() {
        tvExamTimer.setText(getString(R.string.lbl_exam_connecting));
        RetrofitClient.getApiService(context).startExamSession(buildSync())
                .enqueue(new ApiCallback<ExamSession>(context, new Callback<ExamSession>() {
                    @Override
                    public void onResponse(Call<ExamSession> call, Response<ExamSession> response) {
                        ExamSession session = response.body();
                        if (session == null || session.isError()) {
                            showMsg(session != null && session.getMsg() != null ? session.getMsg() : getString(R.string.err_server));
                            continueOffline();
                            return;
                        }
                        handleSession(session, true);
                    }

                    @Override
                    public void onFailure(Call<ExamSession> call, Throwable t) {
                        Log.e(TAG, "startSession failed: " + t.getMessage());
                        continueOffline();
                    }
                }));
    }

    /** No connection: keep going with the time saved on the device; answers are sent when the network is back. */
    private void continueOffline() {
        int remaining = draft.estimateRemainingSeconds(durationInMinute);
        if (remaining <= 0) {
            onTimeExpired(false);
            return;
        }
        showMsg(getString(R.string.lbl_exam_offline_mode));
        remainingSeconds = remaining;
        if (recyclerView.getAdapter() == null) {
            getQuestionsForExamFromApi();
        }
    }

    private void handleSession(ExamSession session, boolean initial) {
        if (session.isSubmitted()) {
            clearDraft();
            finishWithMessage(getString(R.string.lbl_exam_already_submitted));
            return;
        }
        if (session.isExpired()) {
            clearDraft();
            finishWithMessage(getString(R.string.lbl_exam_time_expired_submitted));
            return;
        }
        if (!initial && !session.isRunning()) {
            return; // a background sync found no session: keep the local timer as it is
        }
        // Running (or NotStarted when the server has no session yet)
        draft.setServerRemaining(session.getRemainingSeconds(), System.currentTimeMillis());
        if (session.getAnswers() != null && session.getVersion() > draft.getVersion()) {
            draft.setAnswersJson(session.getAnswers());
            draft.setVersion(session.getVersion());
        }
        saveDraft();
        if (initial) {
            remainingSeconds = session.getRemainingSeconds();
            if (recyclerView.getAdapter() == null) {
                getQuestionsForExamFromApi();
            }
        } else if (timer != null && Math.abs(session.getRemainingSeconds() - remainingSeconds) > 5) {
            // keep the display aligned with the server clock
            remainingSeconds = session.getRemainingSeconds();
            fireUpTheTimer(remainingSeconds);
        }
    }

    /* ------------------------------------------------------------------ drafts */

    private void saveDraft() {
        getPrefManager().setExamDraft(userId, examId, isReExam, draft);
    }

    private void clearDraft() {
        getPrefManager().removeExamDraft(userId, examId, isReExam);
    }

    private Exam buildExamForPost() {
        List<Question> questionList = ((ExamRecyclerViewAdapter) recyclerView.getAdapter()).getQuestionList();
        Exam newExam = new Exam();
        newExam.setId(examId);
        newExam.setUserId(userId);
        newExam.setDuration(durationInMinute);
        newExam.setStartAt(startAt);
        newExam.setQuestions(questionList);
        newExam.setReExam(isReExam);
        newExam.setDraftVersion(draft.getVersion());
        return newExam;
    }

    private String buildAnswersJson() {
        return RetrofitClient.getGson(RetrofitClient.QUESTION_SERIALIZER).toJson(buildExamForPost(), Exam.class);
    }

    /** Called by the adapter on every change: keep the local copy up to date and send it soon. */
    private void onAnswerChanged() {
        if (isFinished || recyclerView.getAdapter() == null) return;
        draft.setVersion(draft.getVersion() + 1);
        draft.setAnswersJson(buildAnswersJson());
        saveDraft();
        dirty = true;
        syncHandler.removeCallbacks(syncRunnable);
        syncHandler.postDelayed(syncRunnable, SYNC_DEBOUNCE_MS);
    }

    private void syncDraft(boolean force) {
        if (isFinished || isSubmitting || syncInFlight || (!dirty && !force)) return;
        syncInFlight = true;
        final int sentVersion = draft.getVersion();
        RetrofitClient.getApiService(context).syncExamDraft(buildSync())
                .enqueue(new ApiCallback<ExamSession>(context, false, new Callback<ExamSession>() {
                    @Override
                    public void onResponse(Call<ExamSession> call, Response<ExamSession> response) {
                        syncInFlight = false;
                        ExamSession session = response.body();
                        if (session == null || session.isError()) return;
                        if (sentVersion == draft.getVersion()) dirty = false;
                        if (!isFinished) handleSession(session, false);
                    }

                    @Override
                    public void onFailure(Call<ExamSession> call, Throwable t) {
                        syncInFlight = false; // stays dirty; retried by the heartbeat
                    }
                }));
    }

    /** Restores the answers saved in the draft into the freshly loaded questions. */
    private void restoreAnswers(List<Question> questionList) {
        if (draft.getAnswersJson() == null || draft.getAnswersJson().isEmpty()) return;
        try {
            Map<Integer, Integer> picked = new HashMap<>();
            JsonObject root = new JsonParser().parse(draft.getAnswersJson()).getAsJsonObject();
            JsonArray questions = root.getAsJsonArray("Q");
            if (questions == null) return;
            for (JsonElement q : questions) {
                JsonArray items = q.getAsJsonObject().getAsJsonArray("I");
                if (items == null) continue;
                for (JsonElement item : items) {
                    JsonArray a = item.getAsJsonArray();
                    if (a.size() >= 2) picked.put(a.get(0).getAsInt(), a.get(1).getAsInt());
                }
            }
            int restored = 0;
            for (Question question : questionList) {
                if (question.getAnswers() == null) continue;
                for (Answer answer : question.getAnswers()) {
                    Integer value = picked.get(answer.getId());
                    if (value != null) {
                        answer.setPickedAnswer(value);
                        if (value != 2) restored++;
                    }
                }
            }
            if (restored > 0) {
                showMsg(getString(R.string.lbl_exam_resumed));
            }
        } catch (Exception e) {
            Log.e(TAG, "restoreAnswers: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------ submit */

    private void submitExamAnswers(final boolean auto) {
        if (isSubmitting || isFinished || recyclerView.getAdapter() == null) return;
        isSubmitting = true;
        syncHandler.removeCallbacks(syncRunnable);
        Exam newExam = buildExamForPost();
        draft.setAnswersJson(buildAnswersJson());
        saveDraft();
        Call<ApiGenericResponse> request = isReExam
                ? RetrofitClient.getApiServiceQuestion(context, RetrofitClient.QUESTION_SERIALIZER).sendPostReExamAnswer(newExam)
                : RetrofitClient.getApiServiceQuestion(context, RetrofitClient.QUESTION_SERIALIZER).sendPostAnswer(newExam);
        request.enqueue(new ApiCallback<ApiGenericResponse>(context, new Callback<ApiGenericResponse>() {
            @Override
            public void onResponse(Call<ApiGenericResponse> call, Response<ApiGenericResponse> response) {
                isSubmitting = false;
                ApiGenericResponse apiGenericResponse = response.body();
                if (apiGenericResponse != null && !apiGenericResponse.isIsError()) {
                    isFinished = true;
                    stopTimer();
                    clearDraft();
                    if (!isReExam) getPrefManager().setNextExamInfo(null);
                    new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                            .setMessage(apiGenericResponse.getMsg())
                            .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    if (isReExam) openSummary();
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    onSubmitFailed(auto);
                }
            }

            @Override
            public void onFailure(Call<ApiGenericResponse> call, Throwable t) {
                isSubmitting = false;
                onSubmitFailed(auto);
            }
        }));
    }

    private void onSubmitFailed(boolean auto) {
        dirty = true;
        new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                .setMessage(auto ? getString(R.string.lbl_exam_submit_failed_auto) : getString(R.string.lbl_exam_submit_failed))
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void openSummary() {
        Intent intent = new Intent(context, AnswerSummaryActivity.class);
        intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, examId);
        intent.putExtra(AppConstants.ExamConstants.INTENT_IS_REEXAM, true);
        startActivity(intent);
        AppUtils.startActivityAnimation(context);
    }

    private void finishWithMessage(String message) {
        isFinished = true;
        stopTimer();
        new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                .setMessage(message)
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /* ------------------------------------------------------------------ questions & timer */

    private void getQuestionsForExamFromApi() {
        RetrofitClient
                .getApiServiceQuestion(context, RetrofitClient.EXAM_DESERIALIZER)
                .getQuestions(examId, false)
                .enqueue(new ApiCallback<List<Question>>(context, new Callback<List<Question>>() {
                    @Override
                    public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                        setDataToAdapter(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Question>> call, Throwable t) {
                        showMsg(getString(R.string.lbl_exam_questions_offline));
                    }
                }));
    }

    private void setDataToAdapter(List<Question> questionList) {
        if (questionList != null && !questionList.isEmpty()) {
            restoreAnswers(questionList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setItemViewCacheSize(questionList.size());
            ExamRecyclerViewAdapter adapter = new ExamRecyclerViewAdapter(context, questionList, examInfo);
            adapter.setOnAnswerChangedListener(new ExamRecyclerViewAdapter.OnAnswerChangedListener() {
                @Override
                public void onAnswerChanged() {
                    ExamActivity.this.onAnswerChanged();
                }
            });
            recyclerView.setAdapter(adapter);

            isExamRunning = true;
            btnSubmit.setEnabled(true);
            if (remainingSeconds <= 0) remainingSeconds = durationInMinute * 60;
            fireUpTheTimer(remainingSeconds);
            syncHandler.removeCallbacks(heartbeatRunnable);
            syncHandler.postDelayed(heartbeatRunnable, SYNC_HEARTBEAT_MS);
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }
    }

    private void fireUpTheTimer(int seconds) {
        stopTimer();
        timer = new CountDownTimer(seconds * 1000L, 1000) {

            public void onTick(long millisUntilFinished) {
                remainingSeconds = (int) (millisUntilFinished / 1000);
                String time = String.format("%02d min, %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                );
                tvExamTimer.setText(getString(R.string.lbl_time_remaining) + time);
            }

            public void onFinish() {
                onTimeExpired(true);
            }
        }.start();
    }

    private void onTimeExpired(boolean canSubmit) {
        recyclerView.setVisibility(View.GONE);
        tvExamTimer.setText(R.string.lbl_time_out);
        isExamRunning = false;
        btnSubmit.setEnabled(false);
        if (canSubmit && recyclerView.getAdapter() != null) {
            submitExamAnswers(true);
        } else {
            // Offline and out of time: the server submits the last saved answers by itself.
            finishWithMessage(getString(R.string.lbl_exam_time_expired_offline));
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /* ------------------------------------------------------------------ navigation */

    private void showGoBackWarning() {
        if (isExamRunning && !isFinished) {
            new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                    .setMessage(getString(R.string.lbl_go_back_warning))
                    .setPositiveButton(getString(R.string.btn_submit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            submitExamAnswers(false);
                        }
                    })
                    .setNeutralButton(getString(R.string.btn_continue_later), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            syncDraft(true);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showGoBackWarning();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showGoBackWarning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isExamRunning && !isFinished && dirty) {
            syncHandler.removeCallbacks(syncRunnable);
            syncDraft(true);
        }
    }

    @Override
    protected void onDestroy() {
        AppConstants.SHOULD_RELOAD_NEXT_EXAM_INFO = true;
        syncHandler.removeCallbacksAndMessages(null);
        stopTimer();
        super.onDestroy();
    }
}
