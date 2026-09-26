package bd.com.ADRENALIN.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * One attempt of the student on an exam, from Exam/GetAttempts: the main exam
 * (attemptNo 0, with a position in the merit list) or a re-exam (attemptNo 1..n).
 */
public class ExamAttempt {
    @SerializedName("Id")
    private long id;
    @SerializedName("AttemptNo")
    private int attemptNo;
    @SerializedName("IsReExam")
    private int isReExam;
    @SerializedName("ExamId")
    private int examId;
    @SerializedName("Name")
    private String name;
    @SerializedName("CategoryId")
    private int categoryId;
    @SerializedName("Marks")
    private double marks;
    @SerializedName("Position")
    private long position;
    @SerializedName("Duration")
    private int duration;
    @SerializedName("CorrectAnswer")
    private int correctAnswer;
    @SerializedName("WrongAnswer")
    private int wrongAnswer;
    @SerializedName("NoAnswer")
    private int noAnswer;
    @SerializedName("TotalAns")
    private int totalAns;
    @SerializedName("MaxMarks")
    private double maxMarks;
    @SerializedName("MinMarks")
    private double minMarks;
    @SerializedName("StartAt")
    private String startAt;

    public long getId() { return id; }
    public int getAttemptNo() { return attemptNo; }
    public boolean isReExam() { return isReExam == 1; }
    public int getExamId() { return examId; }
    public String getName() { return name; }
    public int getCategoryId() { return categoryId; }
    public double getMarks() { return marks; }
    public long getPosition() { return position; }
    public int getDuration() { return duration; }
    public int getCorrectAnswer() { return correctAnswer; }
    public int getWrongAnswer() { return wrongAnswer; }
    public int getNoAnswer() { return noAnswer; }
    public int getTotalAns() { return totalAns; }
    public double getMaxMarks() { return maxMarks; }
    public double getMinMarks() { return minMarks; }
    public String getStartAt() { return startAt; }

    /** Tab label: "Exam" for the main attempt, "Reexam 1", "Reexam 2", ... for re-exams. */
    public String getLabel() {
        return isReExam() ? "Reexam " + attemptNo : "Exam";
    }
}
