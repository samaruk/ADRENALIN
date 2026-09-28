package bd.com.ADRENALIN.pojo.content;

import java.util.List;

/** A lecture, whether it is locked, and its files. */
public class LectureDetailResponse {
    public boolean IsError;
    public String Msg;
    public boolean Locked;
    public String LockedMessage;
    public LectureDetail Lecture;
    public List<ContentFileItem> Files;
}
