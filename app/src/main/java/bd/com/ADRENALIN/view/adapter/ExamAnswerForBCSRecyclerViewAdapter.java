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

import bd.com.ADRENALIN.pojo.Answer;
import bd.com.ADRENALIN.pojo.Question;
import bd.com.ADRENALIN.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by iqrasys on 9/6/2017.
 */
public class ExamAnswerForBCSRecyclerViewAdapter extends
        RecyclerView.Adapter<ExamAnswerForBCSRecyclerViewAdapter.RoutineViewHolder> {

    private List<Question> questionList;
    private Context context;
    private ArrayList<Integer> trackOfRanderedAnswerInList;

    public class RoutineViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvQuestionNo)
        TextView tvQuestionNo;
        @BindView(R.id.tvQuestionTitle)
        TextView title;
        @BindView(R.id.llAnswersOfQuestion)
        LinearLayout llAnswersOfQuestion;


        public RoutineViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getAdapterPosition();
                }
            });
        }
    }

    public ExamAnswerForBCSRecyclerViewAdapter(Context context, List<Question> items) {
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
    public void onBindViewHolder(RoutineViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.title.setText((position + 1) + ". " + question.getQuestionName());

        if (trackOfRanderedAnswerInList.get(position) == null || trackOfRanderedAnswerInList.get(position) == 0) {
            List<Answer> answerList = question.getAnswers();
            CheckBox correctAns=null;
            boolean isWrong=false;
            for (final Answer answer : answerList) {
                View view = LayoutInflater.from(context).inflate(R.layout.layout_answer_option_disable_checkbox, null);
                TextView tvAnswer = view.findViewById( R.id.tvAnsTitle);
                tvAnswer.setText(answer.getText());

                CheckBox isCorrect = ((CheckBox) view.findViewById( R.id.chkIsCorrect));
                if (answer.getAnswerStatus() == 1){
                    isCorrect.setChecked(true);
                    if (answer.getPickedAnswer() == 1){
                        isCorrect.setBackgroundResource(R.drawable.rbtn_green);
                    }else {
                        isCorrect.setBackgroundResource(R.drawable.rbtn_yellow);
                        correctAns=isCorrect;
                    }
                }else if(answer.getPickedAnswer() == 1){
                    isWrong=true;
                    isCorrect.setBackgroundResource(R.drawable.rbtn_orange_red);
                    isCorrect.setChecked(true);
                }
                holder.llAnswersOfQuestion.addView(view);
            }
            if(isWrong&&correctAns!=null){
                correctAns.setBackgroundResource(R.drawable.rbtn_green);
            }
            trackOfRanderedAnswerInList.add(position, 1);
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}