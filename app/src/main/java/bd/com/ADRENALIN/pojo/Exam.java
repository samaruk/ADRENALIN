package bd.com.ADRENALIN.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mahfuz on 7/2/2017.
 */

public class Exam {

    @SerializedName("Id")
    @Expose
    private int id;

    @SerializedName("CategoryId")
    @Expose
    private int categoryId;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("U")
    @Expose
    private String userId;

    @SerializedName("C")
//    @Expose
    private int correctAnswersTotal;
    @SerializedName("W")
//    @Expose
    private int wrongAnswersTotal;
    @SerializedName("N")
//    @Expose
    private int noAnswersTotal;
    @SerializedName("D")
    @Expose
    private int duration;
    @SerializedName("StartAt")
//    @Expose
    private String startAt; // Format is dd/MM/yyyy HH:mm

    @SerializedName("EndAt")
    @Expose
    private String endAt; // Format is dd/MM/yyyy HH:mm

    @SerializedName("TotalQuestion")
    @Expose
    private int totalQuestion;

    @SerializedName("Phone")
    @Expose
    private String questionerPhoneNo;

    @SerializedName("Content")
    @Expose
    private String content;

    @SerializedName("Question")
    @Expose
    private List<Question> questions;


    public int getId() {
        return id;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public int getCategoryId() {
        return categoryId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCorrectAnswersTotal() {
        return correctAnswersTotal;
    }

    public void setCorrectAnswersTotal(int correctAnswersTotal) {
        this.correctAnswersTotal = correctAnswersTotal;
    }

    public int getWrongAnswersTotal() {
        return wrongAnswersTotal;
    }

    public void setWrongAnswersTotal(int wrongAnswersTotal) {
        this.wrongAnswersTotal = wrongAnswersTotal;
    }

    public int getNoAnswersTotal() {
        return noAnswersTotal;
    }

    public void setNoAnswersTotal(int noAnswersTotal) {
        this.noAnswersTotal = noAnswersTotal;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public int getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public String getQuestionerPhoneNo() {
        return questionerPhoneNo;
    }

    public void setQuestionerPhoneNo(String questionerPhoneNo) {
        this.questionerPhoneNo = questionerPhoneNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", correctAnswersTotal=" + correctAnswersTotal +
                ", wrongAnswersTotal=" + wrongAnswersTotal +
                ", noAnswersTotal=" + noAnswersTotal +
                ", duration=" + duration +
                ", startAt='" + startAt + '\'' +
                ", endAt='" + endAt + '\'' +
                ", totalQuestion=" + totalQuestion +
                ", questionerPhoneNo='" + questionerPhoneNo + '\'' +
                ", content='" + content + '\'' +
                ", questions=" + questions +
                '}';
    }
}
