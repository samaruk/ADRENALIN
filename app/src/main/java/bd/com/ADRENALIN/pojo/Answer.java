package bd.com.ADRENALIN.pojo;

/**
 * Created by mahfuz on 7/1/2017.
 */

public class Answer {

    private int id;
    private String text; // Use transient to exclude this field from JSON serialization.
    private int answerStatus;
    private int pickedAnswer = 2;

    public Answer() {
    }

    public Answer(int id, String text, int answerStatus) {
        this.id = id;
        this.text = text;
        this.answerStatus = answerStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(int answerStatus) {
        this.answerStatus = answerStatus;
    }

    public int getPickedAnswer() {
        return pickedAnswer;
    }

    public void setPickedAnswer(int pickedAnswer) {
        this.pickedAnswer = pickedAnswer;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", answerStatus=" + answerStatus +
                ", pickedAnswer=" + pickedAnswer +
                '}';
    }
}
