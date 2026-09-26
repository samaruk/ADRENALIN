package bd.com.ADRENALIN.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Response of Exam/StartSession, Exam/SyncDraft and Exam/Session.
 * The server clock decides how much time is left; the app only displays it.
 */
public class ExamSession {
    public static final String STATUS_NOT_STARTED = "NotStarted";
    public static final String STATUS_RUNNING = "Running";
    public static final String STATUS_EXPIRED = "Expired";
    public static final String STATUS_SUBMITTED = "Submitted";

    @SerializedName("IsError")
    private boolean isError;
    @SerializedName("Msg")
    private String msg;
    @SerializedName("Status")
    private String status;
    @SerializedName("RemainingSeconds")
    private int remainingSeconds;
    @SerializedName("DurationMinutes")
    private int durationMinutes;
    @SerializedName("Version")
    private int version;
    @SerializedName("Answers")
    private String answers;
    @SerializedName("AnswerId")
    private long answerId;
    @SerializedName("IsReExam")
    private boolean isReExam;
    @SerializedName("ServerTime")
    private String serverTime;

    public boolean isError() { return isError; }
    public String getMsg() { return msg; }
    public String getStatus() { return status == null ? "" : status; }
    public int getRemainingSeconds() { return remainingSeconds; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getVersion() { return version; }
    public String getAnswers() { return answers; }
    public long getAnswerId() { return answerId; }
    public boolean isReExam() { return isReExam; }
    public String getServerTime() { return serverTime; }

    public boolean isRunning() { return STATUS_RUNNING.equals(getStatus()); }
    public boolean isExpired() { return STATUS_EXPIRED.equals(getStatus()); }
    public boolean isSubmitted() { return STATUS_SUBMITTED.equals(getStatus()); }
}
