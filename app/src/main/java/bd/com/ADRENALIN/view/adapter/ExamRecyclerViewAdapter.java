package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Answer;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.Question;
import bd.com.ADRENALIN.util.LOG;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/6/17.
 */

public class ExamRecyclerViewAdapter extends
        RecyclerView.Adapter<ExamRecyclerViewAdapter.RoutineViewHolder> {

    private List<Question> questionList;
    private Context context;
    private ArrayList<Integer> trackOfRanderedAnswerInList;
    private Exam examInfo;
    private boolean isPrevent=false;

    public class RoutineViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public Answer selectedAnswer;
        private List<CheckBox> options;
        private boolean isPrevent=false;

        @BindView(R.id.cv)
        CardView cardView;
        @BindView(R.id.tvQuestionNo)
        TextView tvQuestionNo;
        @BindView(R.id.tvQuestionTitle)
        TextView title;
        @BindView(R.id.llAnswersOfQuestion)
        LinearLayout llAnswersOfQuestion;


        public RoutineViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getAdapterPosition();
                }
            });
        }


        private void SetSingleChoiceListener(CheckBox btn,final Answer answer){
            btn.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(isPrevent){
                        isPrevent=false;
                        return;
                    }
                    if(b){
                        answer.setPickedAnswer(1);
                        for (CheckBox option:options ) {
                            if(option.isChecked()&& !option.equals(compoundButton)) {
                                isPrevent = true;
                                option.setChecked(false);
                            }
                        }

                    }else {
                        answer.setPickedAnswer(0);
                    }
                    Log.e("RadioGroup", "setOnCheckedChangeListener => value :" + answer.getPickedAnswer());
                }
            });
        }
    }


    public ExamRecyclerViewAdapter(Context context, List<Question> items, Exam examInfo) {
        this.context = context;
        this.questionList = items;
        this.examInfo=examInfo;
        trackOfRanderedAnswerInList = new ArrayList<>();
        int size = questionList.size();
        for (int i = 0; i < size; i++) {
            trackOfRanderedAnswerInList.add(0);
            Question question = questionList.get(i);
            if(question.getTypeId()==1){
                for (Answer answer : question.getAnswers()) {
                    answer.setPickedAnswer(0);
                }
            }
        }
    }
    private void SetSingleChoiceListener(final CheckBox checkBox,final Answer answer,final RoutineViewHolder holder){
        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.checkBox!=null){
                    holder.checkBox.setChecked(false);
                }
                if(b){
                    answer.setPickedAnswer(1);
                    holder.checkBox=checkBox;
                }else {
                    answer.setPickedAnswer(2);
                    holder.checkBox=null;
                }
                LOG.e("CheckedChange", new Gson().toJson(answer));
            }
        });
    }
    private void SetMultiChoiceListener(final CheckBox checkBox,final Answer answer,final RoutineViewHolder holder){
        answer.setPickedAnswer(0);
        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    answer.setPickedAnswer(1);
                    holder.checkBox=checkBox;
                }else {
                    answer.setPickedAnswer(0);
                    holder.checkBox=null;
                }
                LOG.e("CheckedChange", new Gson().toJson(answer));
            }
        });
    }
    private void SetYesNoListener(CheckBox btn,final CheckBox altBtn,final Answer answer, final int value){
        btn.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(isPrevent){
                    isPrevent=false;
                    return;
                }
                if(b){
                    answer.setPickedAnswer(value);
                    if(altBtn.isChecked()) {
                        isPrevent = true;
                        altBtn.setChecked(false);
                    }
                }else {
                    answer.setPickedAnswer(2);
                }
                LOG.e("CheckedChange", new Gson().toJson(answer));
            }
        });
    }
    @Override
    public RoutineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RoutineViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_exam, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(RoutineViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.title.setText((position + 1) + ". " + question.getQuestionName());

        if (trackOfRanderedAnswerInList.get(position) == null || trackOfRanderedAnswerInList.get(position) == 0) {
            List<Answer> answerList = question.getAnswers();
            if(question.getTypeId()==0){
                holder.options=new ArrayList<CheckBox>();
                for (final Answer answer : answerList) {
                    View view = LayoutInflater.from(context).inflate(R.layout.layout_answer_option_checkbox, null);
                    TextView tvAnswer = view.findViewById( R.id.tvAnsTitle);
                    tvAnswer.setText(answer.getText());

                    CheckBox radioGroup = view.findViewById( R.id.chkIsCorrect);
                    holder.options.add(radioGroup);
                    SetSingleChoiceListener(radioGroup,answer,holder);

                    holder.llAnswersOfQuestion.addView(view);
                }
            }else if(question.getTypeId()==1){
                for (final Answer answer : answerList) {
                    View view = LayoutInflater.from(context).inflate(R.layout.layout_answer_option_checkbox, null);
                    TextView tvAnswer = view.findViewById( R.id.tvAnsTitle);
                    tvAnswer.setText(answer.getText());

                    CheckBox radioGroup = view.findViewById( R.id.chkIsCorrect);
                    SetMultiChoiceListener(radioGroup,answer,holder);

                    holder.llAnswersOfQuestion.addView(view);
                }
            }else {
                for ( Answer answer : answerList) {
                    View view = LayoutInflater.from(context).inflate(R.layout.layout_answer_option, null);
                    TextView tvAnswer = view.findViewById( R.id.tvAnsTitle);
                    tvAnswer.setText(answer.getText());

                    RadioGroup radioGroup = view.findViewById( R.id.rgAnswers);
                    CheckBox rbTrue = view.findViewById( R.id.rbTrue);
                    CheckBox rbFalse = view.findViewById( R.id.rbFalse);
                    SetYesNoListener(rbTrue,rbFalse,answer,1);
                    SetYesNoListener(rbFalse,rbTrue,answer,0);
                    holder.llAnswersOfQuestion.addView(view);

                }
            }

            trackOfRanderedAnswerInList.add(position, 1);
        }

    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}
