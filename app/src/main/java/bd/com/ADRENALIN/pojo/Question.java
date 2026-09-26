package bd.com.ADRENALIN.pojo;

import java.util.List;

public class Question {

    private int questionId;
    private String questionName;
    public String Explanation;
    public int TypeId;
    private List<Answer> answers;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public int getTypeId() {
        return TypeId;
    }

    public void setTypeId(int typeId) {
        this.TypeId = typeId;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                "TypeId=" + TypeId +
                "Explanation=" + Explanation +
                ", questionName='" + questionName + '\'' +
                ", answers=" + answers +
                '}';
    }
}
