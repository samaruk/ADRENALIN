package bd.com.ADRENALIN.pojo.content;

import java.util.List;

/** One lecture with its texts (empty while it is locked). */
public class LectureDetail {
    public long Id;
    public long SubjectId;
    public String Subject;
    public String Course;
    public String Title;
    public int LectureNo;
    public String Topic;
    public String ShortDescription;
    public String Content;
    public String VideoUrl;
    public String PdfUrl;
    public String SlidesUrl;
    public String Duration;
    public List<String> Objectives;
    public String Notes;
    public boolean IsFree;
    public String UpdatedAt;
}
