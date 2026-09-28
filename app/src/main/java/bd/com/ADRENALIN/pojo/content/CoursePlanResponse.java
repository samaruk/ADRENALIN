package bd.com.ADRENALIN.pojo.content;

import java.util.List;

/** A course and its plan, one point per item. */
public class CoursePlanResponse {
    public boolean IsError;
    public String Msg;
    public Course Course;
    public List<String> Points;
    public String UpdatedAt;
}
