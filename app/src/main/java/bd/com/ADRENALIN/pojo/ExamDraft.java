package bd.com.ADRENALIN.pojo;

/**
 * Local copy of a running exam attempt, kept in SharedPreferences so the student can
 * continue after the app is closed, crashes or loses the connection.
 */
public class ExamDraft {
    /** Device time (millis) when the attempt started on this device. */
    private long startedAtMillis;
    /** Version counter, increased on every change; the server keeps the newest version. */
    private int version;
    /** Answers in the Exam/PostAnswer JSON shape, or null when nothing was answered yet. */
    private String answersJson;
    /** Remaining seconds reported by the server, and the device time it was reported at. */
    private int serverRemainingSeconds = -1;
    private long serverRemainingAtMillis;
    /** The exam (as passed to ExamActivity) so the attempt can be reopened from Home. */
    private String examJson;
    private boolean reExam;

    public long getStartedAtMillis() { return startedAtMillis; }
    public void setStartedAtMillis(long startedAtMillis) { this.startedAtMillis = startedAtMillis; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getAnswersJson() { return answersJson; }
    public void setAnswersJson(String answersJson) { this.answersJson = answersJson; }
    public int getServerRemainingSeconds() { return serverRemainingSeconds; }
    public long getServerRemainingAtMillis() { return serverRemainingAtMillis; }
    public void setServerRemaining(int seconds, long atMillis) {
        this.serverRemainingSeconds = seconds;
        this.serverRemainingAtMillis = atMillis;
    }
    public String getExamJson() { return examJson; }
    public void setExamJson(String examJson) { this.examJson = examJson; }
    public boolean isReExam() { return reExam; }
    public void setReExam(boolean reExam) { this.reExam = reExam; }

    /** Seconds elapsed on this device since the attempt started. */
    public int getElapsedSeconds() {
        if (startedAtMillis <= 0) return 0;
        return (int) Math.max(0, (System.currentTimeMillis() - startedAtMillis) / 1000);
    }

    /**
     * Best local estimate of the remaining time: the last server value minus the time
     * passed since, or the duration minus the local elapsed time when the server was never reached.
     */
    public int estimateRemainingSeconds(int durationMinutes) {
        if (serverRemainingSeconds >= 0 && serverRemainingAtMillis > 0) {
            long passed = (System.currentTimeMillis() - serverRemainingAtMillis) / 1000;
            return (int) Math.max(0, serverRemainingSeconds - passed);
        }
        return Math.max(0, durationMinutes * 60 - getElapsedSeconds());
    }
}
