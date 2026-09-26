package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ApiGenericResponse;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.Question;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.view.adapter.ExamRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class ExamActivity extends BaseActivity {

    public static final String TAG = ExamActivity.class.getSimpleName();
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
    private Exam examInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        ButterKnife.bind(this);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        Exam examInfo = getPrefManager().getNexExamInfo();
        Intent intent = getIntent();
        if (intent != null) {
            String examString = intent.getStringExtra(AppConstants.ExamConstants.INTENT_CODE);
            if (examString != null && !examString.isEmpty()) {

                examInfo = new Gson().fromJson(examString, Exam.class);
                Log.e(TAG,examString);
                ExamActivity.this.setTitle(examInfo.getName());

                examId = examInfo.getId();
                durationInMinute = examInfo.getDuration();

                startAt = AppUtils.getSlashSeparatedDateStringFromDate(Calendar.getInstance().getTime());
                isExamRunning = false;

                getQuestionsForExamFromApi();
            }
        }


        btnSubmit.setEnabled(false);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog).setMessage(R.string.lbl_exam_submit_confirmation)
                        .setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                submitExamAnswers();
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
    }

    private void submitExamAnswers() {
        List<Question> questionList = ((ExamRecyclerViewAdapter) recyclerView.getAdapter()).getQuestionList();

        Exam newExam = new Exam();
        newExam.setId(examId);
        newExam.setUserId(getPrefManager().getUserInfo().getId());
        newExam.setDuration(durationInMinute);
        newExam.setStartAt(startAt);
        newExam.setQuestions(questionList);
        Call<ApiGenericResponse> request;
        request= RetrofitClient.getApiServiceQuestion(context, RetrofitClient.QUESTION_SERIALIZER).sendPostAnswer(newExam);
        //if(examInfo.getCategoryId()==1){
            //request=RetrofitClient.getApiServiceQuestion(context, RetrofitClient.QUESTION_SERIALIZER)
           //         .sendPostAnswerForBCS(newExam);
        //}else {

        //}
        request.enqueue(new ApiCallback<ApiGenericResponse>(context, new Callback<ApiGenericResponse>() {
                    @Override
                    public void onResponse(Call<ApiGenericResponse> call, Response<ApiGenericResponse> response) {
                        ApiGenericResponse apiGenericResponse = response.body();
                        if (!apiGenericResponse.isIsError()) {

                            new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                                    .setMessage(apiGenericResponse.getMsg())
                                    .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            getPrefManager().setNextExamInfo(null);
                                            finish();
                                        }
                                    })
                                    .setCancelable(true)
                                    .show();

                        } else {
                            showMsg("Error in posting Exam !!!");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiGenericResponse> call, Throwable t) {
                    }
                }));
    }

    private void getQuestionsForExamFromApi() {
         /* Get all Questions for an Exam */
        RetrofitClient
                .getApiServiceQuestion(context, RetrofitClient.EXAM_DESERIALIZER)
                .getQuestions(examId,false)
                .enqueue(new ApiCallback<List<Question>>(context, new Callback<List<Question>>() {
                    @Override
                    public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                        List<Question> questionList = response.body();
/*                        LOG.e(TAG, "Questiond : " +questionList.toString());
                        LOG.e(TAG, "examId : " +examId);
                        LOG.e(TAG, "examInfo : " +examInfo);*/
                        setDataToAdapter(questionList);
                    }

                    @Override
                    public void onFailure(Call<List<Question>> call, Throwable t) {
                    }
                }));

    }

    private void setDataToAdapter(List<Question> questionList) {
        if (questionList != null && !questionList.isEmpty()) {


            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setItemViewCacheSize(questionList.size());
            recyclerView.setAdapter(new ExamRecyclerViewAdapter(context, questionList,examInfo));

            isExamRunning = true;
            btnSubmit.setEnabled(true);
            fireUpTheTimer();
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }

    }

    private void fireUpTheTimer() {
        showMsg("Exam starts at " + AppUtils.getTimeStringFromDate(Calendar.getInstance().getTime()));
        new CountDownTimer(durationInMinute * 60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                String time = String.format("%02d min, %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                );
                tvExamTimer.setText(getString(R.string.lbl_time_remaining) + time);
            }

            public void onFinish() {
                recyclerView.setVisibility(View.GONE);
                tvExamTimer.setText(R.string.lbl_time_out);
                isExamRunning = false;

                submitExamAnswers();
            }
        }.start();
    }

    private void showGoBackWarning() {
        if (isExamRunning)
            new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog).setMessage(getString(R.string.lbl_go_back_warning))
                    .setPositiveButton(getString(R.string.btn_quit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            submitExamAnswers();
//                            finish();
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
        else
            finish();
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
    protected void onDestroy() {
        AppConstants.SHOULD_RELOAD_NEXT_EXAM_INFO = true;
        super.onDestroy();
    }
}
