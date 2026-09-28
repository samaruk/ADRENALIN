package bd.com.ADRENALIN.pojo.content;

import java.util.List;

/** Visible notices, pinned first then latest. */
public class NoticeFeedResponse {
    public boolean IsError;
    public String Msg;
    public long LatestId;
    public List<NoticeItem> Data;
}
