package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.pojo.ResponseModel.QuestionExplanation;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Answer;
import bd.com.ADRENALIN.pojo.Question;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by iqrasys on 9/6/2017.
 */


public class ExamAnswerOnlyForBCSRecyclerViewAdapter extends
        RecyclerView.Adapter<ExamAnswerOnlyForBCSRecyclerViewAdapter.RoutineViewHolder> {

    private List<Question> questionList;
    private Context context;
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

        }
    }

    public ExamAnswerOnlyForBCSRecyclerViewAdapter(Context context, List<Question> items) {
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
        holder.txtShortExplanation.setText(question.Explanation);
        holder.question=question;
        if (trackOfRanderedAnswerInList.get(position) == null || trackOfRanderedAnswerInList.get(position) == 0) {
            List<Answer> answerList = question.getAnswers();
            for (final Answer answer : answerList) {
                View view = LayoutInflater.from(context).inflate(R.layout.layout_answer_option_disable_checkbox, null);
                TextView tvAnswer = view.findViewById( R.id.tvAnsTitle);
                tvAnswer.setText(answer.getText());
                CheckBox isCorrect = ((CheckBox) view.findViewById( R.id.chkIsCorrect));
                if (answer.getAnswerStatus() == 1) {
                    isCorrect.setBackgroundResource(R.drawable.rbtn_green);
                    isCorrect.setChecked(true);
                }
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
