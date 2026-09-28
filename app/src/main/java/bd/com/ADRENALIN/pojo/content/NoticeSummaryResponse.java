package bd.com.ADRENALIN.pojo.content;

import java.util.List;

/** Visible notices for the unread badge. */
public class NoticeSummaryResponse {
    public boolean IsError;
    public String Msg;
    public int Count;
    public long LatestId;
    public String LatestTitle;
    public String LatestAt;
    public List<NoticeStamp> Items;
}
