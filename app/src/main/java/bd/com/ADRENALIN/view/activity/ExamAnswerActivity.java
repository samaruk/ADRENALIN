package bd.com.ADRENALIN.view.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.Question;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.view.adapter.ExamAnswerRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/9/17.
 */

public class ExamAnswerActivity extends BaseActivity {
    public static final String TAG = ExamAnswerActivity.class.getSimpleName();
    private ExamAnswerActivity context;
    private boolean fromArchive;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_answer);
        ButterKnife.bind(this);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ExamAnswerActivity.this.setTitle(getString(R.string.lbl_answer));

        Intent intent = getIntent();
        if (intent != null) {

            fromArchive = intent.getBooleanExtra(AppConstants.ExamConstants.INTENT_FROM_ARCHIVE, false);
            if (fromArchive){
                  //categoryId =intent.getIntExtra(AppConstants.ExamConstants.INTENT_EXAM_CATEGORY, 0);
                int categoryId =intent.getIntExtra(AppConstants.ExamConstants.INTENT_EXAM_CATEGORY, 0);
                getExamQuestionFromAPI(intent.getIntExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, 0),categoryId);
            }
            else
                getResultFromAPI(intent.getIntExtra(AppConstants.ExamConstants.INTENT_ANSWER_ID, 0),
                        intent.getBooleanExtra(AppConstants.ExamConstants.INTENT_IS_REEXAM, false));
        }
    }

    private void getExamQuestionFromAPI(int examId,final int categoryId) {
        /* Get all Questions for an Exam */
        try {
            RetrofitClient
                    .getApiServiceQuestion(context, RetrofitClient.EXAM_DESERIALIZER)
                    .getQuestions(examId,true)
                    .enqueue(new ApiCallback<List<Question>>(context, new Callback<List<Question>>() {
                        @Override
                        public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                            List<Question> questionList = response.body();
                            //LOG.e(TAG, questionList.toString());
                            Log.e("SamarukWebSocket","getExamQuestionFromAPI Size :-   "+questionList.size());
                            if (questionList != null && !questionList.isEmpty())
                                setDataToAdapter(questionList,categoryId);
                        }

                        @Override
                        public void onFailure(Call<List<Question>> call, Throwable t) {
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket","getExamQuestionFromAPI Exception :-   "+e.getMessage());
        }

    }

    private void getResultFromAPI(int answerId, boolean isReExam) {
        /* Get all Answers for an Exam */
        answerCall(answerId, isReExam).enqueue(new ApiCallback<Exam>(context, new Callback<Exam>() {
                    @Override
                    public void onResponse(Call<Exam> call, Response<Exam> response) {
                        Exam exam = response.body();
                        List<Question> questionList = exam.getQuestions();
                        Log.e(TAG, new Gson().toJson(exam));
                        //exam.setCategoryId(1);
                        if (questionList != null && !questionList.isEmpty())
                            setDataToAdapter(questionList,exam.getCategoryId());
                    }

                    @Override
                    public void onFailure(Call<Exam> call, Throwable t) {
                    }
                }));

    }

    private void setDataToAdapter(List<Question> questionList,int examCategoryId) {
        Log.e(TAG, new Gson().toJson(questionList));
        if (questionList != null && !questionList.isEmpty()) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setItemViewCacheSize(questionList.size());

            recyclerView.setAdapter(new ExamAnswerRecyclerViewAdapter(context, questionList));
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }
    }

    /** Main exam answers come from Exam/GetAnswer, re-exam answers from Exam/GetReAnswer. */
    private Call<Exam> answerCall(int answerId, boolean isReExam) {
        if (isReExam) {
            return RetrofitClient.getApiServiceQuestion(context, RetrofitClient.EXAM_DESERIALIZER_FOR_ALL_ANSWERS).getReAnswersById(answerId);
        }
        return RetrofitClient.getApiServiceQuestion(context, RetrofitClient.EXAM_DESERIALIZER_FOR_ALL_ANSWERS).getAnswersByExamId(answerId);
    }
}
