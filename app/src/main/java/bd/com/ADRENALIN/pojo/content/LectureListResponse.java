package bd.com.ADRENALIN.pojo.content;

import java.util.List;

/** Lectures of a subject. */
public class LectureListResponse {
    public boolean IsError;
    public String Msg;
    public LectureSubject Subject;
    public boolean HasAccess;
    public List<LectureItem> Data;
}
