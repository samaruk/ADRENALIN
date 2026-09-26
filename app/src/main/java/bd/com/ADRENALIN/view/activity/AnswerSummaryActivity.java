package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.AnswerSummary;
import bd.com.ADRENALIN.pojo.ExamAttempt;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.util.AppConstants;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Result of one exam with a tab per attempt: "Exam" (the main exam, with position and
 * merit list) and "Reexam 1..n" (re-exams, which are not part of the competition).
 *
 * Created by mahfuz on 7/9/17.
 */
public class AnswerSummaryActivity extends BaseActivity {

    public static final String TAG = AnswerSummaryActivity.class.getSimpleName();
    private Context context;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvAnswerSummaryOf)
    TextView tvAnswerSummaryOf;
    @BindView(R.id.tvNumberOfCandidates)
    TextView tvNumberOfCandidates;
    @BindView(R.id.tvMarks)
    TextView tvMarks;
    @BindView(R.id.tvMaxMark)
    TextView tvMaxMark;
    @BindView(R.id.tvMinMark)
    TextView tvMinMark;
    @BindView(R.id.tvPosition)
    TextView tvPosition;
    @BindView(R.id.llPositionRow)
    View llPositionRow;
    @BindView(R.id.tvCorrectAnswer)
    TextView tvCorrectAnswer;
    @BindView(R.id.tvWrongAnswer)
    TextView tvWrongAnswer;
    @BindView(R.id.tvNoAnswer)
    TextView tvNoAnswer;
    @BindView(R.id.btnShowQuestion)
    AppCompatButton btnShowQuestion;
    @BindView(R.id.btnExamDiscussion)
    AppCompatButton btnExamDiscussion;
    @BindView(R.id.btnMeritList)
    AppCompatButton btnMeritList;
    @BindView(R.id.tvAttemptsLabel)
    TextView tvAttemptsLabel;
    @BindView(R.id.hsAttemptTabs)
    View hsAttemptTabs;
    @BindView(R.id.llAttemptTabs)
    LinearLayout llAttemptTabs;

    private int examId;
    private boolean preferReExam;
    private AnswerSummary legacySummary;
    private List<ExamAttempt> attempts = new ArrayList<>();
    private int selectedIndex = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_summary);
        ButterKnife.bind(this);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AnswerSummaryActivity.this.setTitle(getString(R.string.lbl_answer_summary));

        Intent intent = getIntent();
        if (intent != null) {
            String json = intent.getStringExtra(AppConstants.AnswerSummaryConstants.ANSWER_SUMMARY_INTENT_CODE);
            if (json != null && !json.isEmpty()) {
                legacySummary = new Gson().fromJson(json, AnswerSummary.class);
                examId = legacySummary.getExamId();
                preferReExam = legacySummary.isReExam();
                showSummary(legacySummary);
            }
            int intentExamId = intent.getIntExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, 0);
            if (intentExamId > 0) examId = intentExamId;
            if (intent.getBooleanExtra(AppConstants.ExamConstants.INTENT_IS_REEXAM, false)) preferReExam = true;
        }
        if (examId > 0) {
            loadAttempts();
        } else if (legacySummary == null) {
            showMsg(getString(R.string.lbl_no_data));
        }
    }

    private void loadAttempts() {
        String userId = getPrefManager().getUserInfo() != null ? getPrefManager().getUserInfo().getId() : "";
        RetrofitClient.getApiService(context).getExamAttempts(userId, examId).enqueue(new ApiCallback<List<ExamAttempt>>(context,
                new Callback<List<ExamAttempt>>() {
                    @Override
                    public void onResponse(Call<List<ExamAttempt>> call, Response<List<ExamAttempt>> response) {
                        List<ExamAttempt> list = response.body();
                        if (list == null || list.isEmpty()) {
                            if (legacySummary == null) showMsg(getString(R.string.lbl_no_data));
                            return;
                        }
                        attempts = list;
                        buildTabs();
                        int index = 0;
                        if (preferReExam) {
                            for (int i = attempts.size() - 1; i >= 0; i--) {
                                if (attempts.get(i).isReExam()) {
                                    index = i;
                                    break;
                                }
                            }
                        }
                        select(index);
                    }

                    @Override
                    public void onFailure(Call<List<ExamAttempt>> call, Throwable t) {
                        LOG.e(TAG, "getExamAttempts: " + t.getMessage());
                        if (legacySummary == null) showMsg(getString(R.string.err_network));
                    }
                }));
    }

    private void buildTabs() {
        llAttemptTabs.removeAllViews();
        boolean show = attempts.size() > 1;
        tvAttemptsLabel.setVisibility(show ? View.VISIBLE : View.GONE);
        hsAttemptTabs.setVisibility(show ? View.VISIBLE : View.GONE);
        if (!show) return;
        int padding = (int) (12 * getResources().getDisplayMetrics().density);
        int margin = (int) (6 * getResources().getDisplayMetrics().density);
        for (int i = 0; i < attempts.size(); i++) {
            final int index = i;
            TextView tab = new TextView(context);
            tab.setText(attempts.get(i).getLabel());
            tab.setPadding(padding, padding / 2, padding, padding / 2);
            tab.setTextSize(15);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, margin, 0);
            tab.setLayoutParams(params);
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(index);
                }
            });
            llAttemptTabs.addView(tab);
        }
    }

    private void select(int index) {
        if (index < 0 || index >= attempts.size()) return;
        selectedIndex = index;
        for (int i = 0; i < llAttemptTabs.getChildCount(); i++) {
            TextView tab = (TextView) llAttemptTabs.getChildAt(i);
            boolean active = i == index;
            tab.setBackgroundColor(ContextCompat.getColor(context, active ? R.color.primary_dark : R.color.gray_light));
            tab.setTextColor(ContextCompat.getColor(context, active ? R.color.white : R.color.text_color_primary));
            tab.setTypeface(null, active ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
        showAttempt(attempts.get(index));
    }

    /** Fallback for older callers that pass the summary row itself. */
    private void showSummary(final AnswerSummary summary) {
        bind(summary.getName(), summary.isReExam(), 0, summary.getMarks(), summary.getPosition(), summary.getMaxMarks(), summary.getMinMarks(),
                summary.getCorrectAnswer(), summary.getWrongAnswer(), summary.getNoAnswer(), summary.getTotalAns(),
                summary.getId(), summary.getExamId());
    }

    private void showAttempt(final ExamAttempt attempt) {
        bind(attempt.getName(), attempt.isReExam(), attempt.getAttemptNo(), attempt.getMarks(), attempt.getPosition(), attempt.getMaxMarks(), attempt.getMinMarks(),
                attempt.getCorrectAnswer(), attempt.getWrongAnswer(), attempt.getNoAnswer(), attempt.getTotalAns(),
                attempt.getId(), attempt.getExamId());
    }

    private void bind(String name, final boolean isReExam, int attemptNo, double marks, long position, double maxMarks, double minMarks,
                      int correct, int wrong, int noAnswer, int totalAns, final long answerId, final int examIdOfAttempt) {
        String title = getString(R.string.lbl_answer_summary_of) + name;
        if (isReExam && attemptNo > 0) title += " (" + getString(R.string.lbl_reexam) + " " + attemptNo + ")";
        tvAnswerSummaryOf.setText(title);
        tvMarks.setText(marks + "");
        tvMaxMark.setText(maxMarks + "");
        tvMinMark.setText(minMarks + "");
        tvPosition.setText(position + "");
        tvCorrectAnswer.setText(correct + "");
        tvWrongAnswer.setText(wrong + "");
        tvNoAnswer.setText(noAnswer + "");
        tvNumberOfCandidates.setText(totalAns + "");

        // Re-exams are not part of the competition: no position, no merit list.
        llPositionRow.setVisibility(isReExam ? View.GONE : View.VISIBLE);
        btnMeritList.setVisibility(isReExam ? View.GONE : View.VISIBLE);

        btnShowQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ExamAnswerActivity.class);
                intent.putExtra(AppConstants.ExamConstants.INTENT_ANSWER_ID, (int) answerId);
                intent.putExtra(AppConstants.ExamConstants.INTENT_IS_REEXAM, isReExam);
                startActivity(intent);
                AppUtils.startActivityAnimation(context);
            }
        });
        btnExamDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ExamDiscussionActivity.class);
                intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, examIdOfAttempt);
                startActivity(intent);
                AppUtils.startActivityAnimation(context);
            }
        });
        btnMeritList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, MeritListActivity.class);
                    intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, examIdOfAttempt);
                    startActivity(intent);
                    AppUtils.startActivityAnimation(context);
                } catch (Exception exp) {
                    LOG.e("MeritListActivity", "error=>" + exp.getMessage());
                }
            }
        });
    }
}
