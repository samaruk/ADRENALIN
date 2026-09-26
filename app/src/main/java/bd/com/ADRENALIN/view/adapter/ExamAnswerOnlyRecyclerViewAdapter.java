package bd.com.ADRENALIN.view.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.Answer;
import bd.com.ADRENALIN.pojo.Question;
import bd.com.ADRENALIN.pojo.ResponseModel.QuestionExplanation;
import bd.com.ADRENALIN.pojo.ResponseModel.ResponseJsonGeneric;
import bd.com.ADRENALIN.view.activity.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/6/17.
 */

public class ExamAnswerOnlyRecyclerViewAdapter extends
        RecyclerView.Adapter<ExamAnswerOnlyRecyclerViewAdapter.RoutineViewHolder> {

    private List<Question> questionList;
    private BaseActivity context;
    private ArrayList<Integer> trackOfRanderedAnswerInList;

    public class RoutineViewHolder extends RecyclerView.ViewHolder {

        boolean isLoaded=false,isOpened=false;
        Question question;
        QuestionExplanation explanation;
        @BindView(R.id.tvQuestionNo)
        TextView tvQuestionNo;
        @BindView(R.id.tvQuestionTitle)
        TextView title;
        @BindView(R.id.llAnswersOfQuestion)
        LinearLayout llAnswersOfQuestion;
        @BindView(R.id.txtShortExplanation)
        TextView txtShortExplanation;
        @BindView(R.id.img_recycler_view)
        RecyclerView imgRecyclerView;


        public RoutineViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            txtShortExplanation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggle();
                }
            });
        }
        private void animate(TextView view,int line){
            /*ObjectAnimator animation = ObjectAnimator.ofInt(
                    view,
                    "maxLines",
                    line);
            animation.setDuration(0);
            animation.start();*/
            txtShortExplanation.setMaxLines(line);
        }
        private void load(){
            if(isLoaded){
                imgRecyclerView.setVisibility(View.VISIBLE);
                if(explanation!=null)
                //txtShortExplanation.setText(explanation.Explanation);
                txtShortExplanation.setMaxLines(99999);
                animate(txtShortExplanation,99999);
            }else {
                isLoaded=true;
                RetrofitClient.getApiService(context).getQuestionExplanation(question.getQuestionId()).enqueue(new ApiCallback< ResponseJsonGeneric<QuestionExplanation>>(context,
                        new Callback<ResponseJsonGeneric<QuestionExplanation>>() {
                            @Override
                            public void onResponse(Call< ResponseJsonGeneric<QuestionExplanation>> call, Response< ResponseJsonGeneric<QuestionExplanation>> response) {
                                ResponseJsonGeneric<QuestionExplanation> result = response.body();
                                if (!result.IsError){
                                    explanation=result.Data;
                                    txtShortExplanation.setText(explanation.Explanation);
                                    animate(txtShortExplanation,99999);
                                    ImageRecyclerViewAdapter imgAdpt=new ImageRecyclerViewAdapter(context,explanation.Images,imgRecyclerView,"Question");
                                }
                                else{

                                }
                            }
                            @Override
                            public void onFailure(Call< ResponseJsonGeneric<QuestionExplanation>> call, Throwable t) {

                            }
                        }));
            }
        }
        private void toggle(){
            try {
                if(isOpened){
                    isOpened=false;
                    //txtShortExplanation.setText(question.Explanation);
                    animate(txtShortExplanation,3);
                    imgRecyclerView.setVisibility(View.GONE);
                }else {
                    isOpened=true;
                    //txtShortExplanation.setText(question.Explanation);
                    load();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SamarukWebSocket","Error in RoutineViewHolder is :-  "+e.getMessage());
            }
        }
    }

    public ExamAnswerOnlyRecyclerViewAdapter(BaseActivity context, List<Question> items) {
        this.context = context;
        this.questionList = items;
        trackOfRanderedAnswerInList = new ArrayList<>();
        int size = questionList.size();
        for (int i = 0; i < size; i++) {
            trackOfRanderedAnswerInList.add(0);
        }
    }

    @Override
    public RoutineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RoutineViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_exam_answer, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final RoutineViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.title.setText((position + 1) + ". " + question.getQuestionName());
        holder.question=question;
        holder.txtShortExplanation.setText(question.Explanation);

        if (trackOfRanderedAnswerInList.get(position) == null || trackOfRanderedAnswerInList.get(position) == 0) {
            List<Answer> answerList = question.getAnswers();
            for (final Answer answer : answerList) {
                View view = LayoutInflater.from(context).inflate(R.layout.layout_answer_option_disable, null);
                TextView tvAnswer = view.findViewById( R.id.tvAnsTitle);
                tvAnswer.setText(answer.getText());

//            RadioGroup radioGroup = ButterKnife.findById(view, R.id.rgAnswers);

                RadioButton rbTrue = ((RadioButton) view.findViewById( R.id.rbTrue));
                RadioButton rbFalse = ((RadioButton)view.findViewById( R.id.rbFalse));

                if (answer.getAnswerStatus() == 1)
                    rbTrue.setBackgroundResource(R.drawable.rbtn_green);
                else if (answer.getAnswerStatus() == 0)
                    rbFalse.setBackgroundResource(R.drawable.rbtn_green);

                holder.llAnswersOfQuestion.addView(view);
            }
            trackOfRanderedAnswerInList.add(position, 1);
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}
