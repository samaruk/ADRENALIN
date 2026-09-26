package bd.com.ADRENALIN.pojo;

import com.google.gson.annotations.SerializedName;

/** Body of Exam/StartSession and Exam/SyncDraft. */
public class DraftSync {
    @SerializedName("U")
    private String userId;
    @SerializedName("Id")
    private int examId;
    @SerializedName("R")
    private boolean reExam;
    @SerializedName("V")
    private int version;
    /** Seconds elapsed on this device since the attempt started; used only if the server has no session yet. */
    @SerializedName("E")
    private int elapsedSeconds;
    /** Raw answer JSON (same shape as Exam/PostAnswer) or null when nothing changed. */
    @SerializedName("A")
    private String answers;

    public DraftSync(String userId, int examId, boolean reExam, int version, int elapsedSeconds, String answers) {
        this.userId = userId;
        this.examId = examId;
        this.reExam = reExam;
        this.version = version;
        this.elapsedSeconds = elapsedSeconds;
        this.answers = answers;
    }
}
