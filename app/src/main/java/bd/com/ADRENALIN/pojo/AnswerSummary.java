package bd.com.ADRENALIN.pojo;

/**
 * Created by mahfuz on 7/4/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnswerSummary implements Parcelable {

    @SerializedName("Id")
    @Expose
    private int id;
    @SerializedName("CategoryId")
    @Expose
    private int categoryId;

    @SerializedName("ExamId")
    @Expose
    private int examId;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Marks")
    @Expose
    private double marks;
    @SerializedName("Position")
    @Expose
    private int position;
    @SerializedName("Duration")
    @Expose
    private int duration;
    @SerializedName("CorrectAnswer")
    @Expose
    private int correctAnswer;
    @SerializedName("WrongAnswer")
    @Expose
    private int wrongAnswer;
    @SerializedName("NoAnswer")
    @Expose
    private int noAnswer;
    @SerializedName("TotalAns")
    @Expose
    private int totalAns;
    @SerializedName("MaxMarks")
    @Expose
    private double maxMarks;
    @SerializedName("MinMarks")
    @Expose
    private double minMarks;

    public final static Parcelable.Creator<AnswerSummary> CREATOR = new Creator<AnswerSummary>() {
        @SuppressWarnings({"unchecked"})
        public AnswerSummary createFromParcel(Parcel in) {
            AnswerSummary instance = new AnswerSummary();
            instance.id = ((int) in.readValue((int.class.getClassLoader())));
            instance.examId = ((int) in.readValue((int.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.marks = ((double) in.readValue((double.class.getClassLoader())));
            instance.position = ((int) in.readValue((int.class.getClassLoader())));
            instance.duration = ((int) in.readValue((int.class.getClassLoader())));
            instance.correctAnswer = ((int) in.readValue((int.class.getClassLoader())));
            instance.wrongAnswer = ((int) in.readValue((int.class.getClassLoader())));
            instance.noAnswer = ((int) in.readValue((int.class.getClassLoader())));
            instance.totalAns = ((int) in.readValue((int.class.getClassLoader())));
            instance.maxMarks = ((double) in.readValue((double.class.getClassLoader())));
            instance.minMarks = ((double) in.readValue((double.class.getClassLoader())));
            return instance;
        }

        public AnswerSummary[] newArray(int size) {
            return (new AnswerSummary[size]);
        }

    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public int getCategoryId() {
        return categoryId;
    }

    public AnswerSummary withId(int id) {
        this.id = id;
        return this;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public AnswerSummary withExamId(int examId) {
        this.examId = examId;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnswerSummary withName(String name) {
        this.name = name;
        return this;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public AnswerSummary withMarks(double marks) {
        this.marks = marks;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public AnswerSummary withPosition(int position) {
        this.position = position;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public AnswerSummary withDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public AnswerSummary withCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
        return this;
    }

    public int getWrongAnswer() {
        return wrongAnswer;
    }

    public void setWrongAnswer(int wrongAnswer) {
        this.wrongAnswer = wrongAnswer;
    }

    public AnswerSummary withWrongAnswer(int wrongAnswer) {
        this.wrongAnswer = wrongAnswer;
        return this;
    }

    public int getNoAnswer() {
        return noAnswer;
    }

    public void setNoAnswer(int noAnswer) {
        this.noAnswer = noAnswer;
    }

    public AnswerSummary withNoAnswer(int noAnswer) {
        this.noAnswer = noAnswer;
        return this;
    }

    public int getTotalAns() {
        return totalAns;
    }

    public void setTotalAns(int totalAns) {
        this.totalAns = totalAns;
    }

    public AnswerSummary withTotalAns(int totalAns) {
        this.totalAns = totalAns;
        return this;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public AnswerSummary withMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
        return this;
    }

    public double getMinMarks() {
        return minMarks;
    }

    public void setMinMarks(double minMarks) {
        this.minMarks = minMarks;
    }

    public AnswerSummary withMinMarks(double minMarks) {
        this.minMarks = minMarks;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(examId);
        dest.writeValue(name);
        dest.writeValue(marks);
        dest.writeValue(position);
        dest.writeValue(duration);
        dest.writeValue(correctAnswer);
        dest.writeValue(wrongAnswer);
        dest.writeValue(noAnswer);
        dest.writeValue(totalAns);
        dest.writeValue(maxMarks);
        dest.writeValue(minMarks);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "AnswerSummary{" +
                "id=" + id +
                ", examId=" + examId +
                ", name='" + name + '\'' +
                ", marks=" + marks +
                ", position=" + position +
                ", duration=" + duration +
                ", correctAnswer=" + correctAnswer +
                ", wrongAnswer=" + wrongAnswer +
                ", noAnswer=" + noAnswer +
                ", totalAns=" + totalAns +
                ", maxMarks=" + maxMarks +
                ", minMarks=" + minMarks +
                '}';
    }
}