package bd.com.ADRENALIN.pojo.content;

import java.util.List;

/** A notice of the app. */
public class NoticeItem {
    public long Id;
    public String Title;
    public String Content;
    public String Category;
    public boolean IsPinned;
    public String PublishAt;
    public String ImageUrl;
    public List<String> Images;
    public List<ContentFileItem> Files;
}
