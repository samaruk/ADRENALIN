package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.AnswerSummary;
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
 * Created by mahfuz on 7/9/17.
 */

public class AnswerSummaryActivity extends BaseActivity {

    public static final String TAG = AnswerSummaryActivity.class.getSimpleName();
    private Context context;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
            AnswerSummary answerSummary = new Gson().fromJson(intent.getStringExtra(AppConstants.AnswerSummaryConstants.ANSWER_SUMMARY_INTENT_CODE), AnswerSummary.class);

            setData(answerSummary);
        }

//        getAnswerSummaryFromAPI();
    }

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



    private void setData(final AnswerSummary answerSummary) {
        tvAnswerSummaryOf.setText(getString(R.string.lbl_answer_summary_of) + answerSummary.getName());

        tvMarks.setText(answerSummary.getMarks() + "");
        tvMaxMark.setText(answerSummary.getMaxMarks() + "");
        tvMinMark.setText(answerSummary.getMinMarks() + "");
        tvPosition.setText(answerSummary.getPosition() + "");

        tvCorrectAnswer.setText(answerSummary.getCorrectAnswer() + "");
        tvWrongAnswer.setText(answerSummary.getWrongAnswer() + "");
        tvNoAnswer.setText(answerSummary.getNoAnswer() + "");
        tvNumberOfCandidates.setText(answerSummary.getTotalAns() + "");

        btnShowQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ExamAnswerActivity.class);
                intent.putExtra(AppConstants.ExamConstants.INTENT_ANSWER_ID, answerSummary.getId());
                startActivity(intent);
                AppUtils.startActivityAnimation(context);
            }
        });
        btnExamDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ExamDiscussionActivity.class);
                intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, answerSummary.getExamId());
                startActivity(intent);
                AppUtils.startActivityAnimation(context);
            }
        });



        btnMeritList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LOG.e("MeritListActivity", "ggggg=>");
                try {
                    Intent intent = new Intent(context, MeritListActivity.class);
                    intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, answerSummary.getExamId());
                    startActivity(intent);
                    AppUtils.startActivityAnimation(context);
                }catch (Exception exp){

                    LOG.e("MeritListActivity", "ggggg=>"+exp.getMessage());
                }
            }
        });

    }

    private void getAnswerSummaryFromAPI() {
        //        /* Answer Summary */
        RetrofitClient.getApiService(context).getAnswerSummery(5).enqueue(new ApiCallback<AnswerSummary>(context,
                new Callback<AnswerSummary>() {
                    @Override
                    public void onResponse(Call<AnswerSummary> call, Response<AnswerSummary> response) {
                        AnswerSummary answerSummary = response.body();
                        LOG.e(TAG, answerSummary.toString());
                    }

                    @Override
                    public void onFailure(Call<AnswerSummary> call, Throwable t) {
                    }
                }));
    }
}
