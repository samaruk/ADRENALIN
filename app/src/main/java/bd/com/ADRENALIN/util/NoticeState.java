package bd.com.ADRENALIN.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bd.com.ADRENALIN.pojo.content.NoticeStamp;

/**
 * Which notices the student has seen (for the unread badge) and which ones already rang (so a new notice
 * plays the sound once, whether the app or the background check finds it first). Stored on the phone.
 */
public final class NoticeState {

    private static final String PREFS = "adrenalin_notices";
    private static final String SEEN = "seen", KNOWN = "known", READY = "ready", BADGE = "badge";
    /** On the first check, notices older than this count as read. */
    private static final long RECENT_MS = 7L * 24 * 60 * 60 * 1000;

    private NoticeState() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static Set<Long> read(SharedPreferences prefs, String key) {
        Set<Long> ids = new HashSet<>();
        String text = prefs.getString(key, "");
        if (TextUtils.isEmpty(text)) return ids;
        for (String part : text.split(",")) {
            try {
                ids.add(Long.parseLong(part.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return ids;
    }

    private static void write(SharedPreferences.Editor editor, String key, Collection<Long> ids) {
        editor.putString(key, TextUtils.join(",", ids));
    }

    private static List<Long> ids(List<NoticeStamp> items) {
        List<Long> ids = new ArrayList<>();
        if (items != null) for (NoticeStamp s : items) ids.add(s.Id);
        return ids;
    }

    /**
     * The visible notices the student has not seen, and the ones that have not rung yet (these are marked as
     * rung now, so only one caller gets them). On the very first call nothing rings and older notices count as read.
     */
    public static synchronized Result update(Context context, List<NoticeStamp> items) {
        SharedPreferences prefs = prefs(context);
        List<Long> visible = ids(items);
        Set<Long> seen = read(prefs, SEEN);
        Set<Long> known = read(prefs, KNOWN);
        SharedPreferences.Editor editor = prefs.edit();
        List<Long> fresh = new ArrayList<>();
        if (!prefs.getBoolean(READY, false)) {
            long now = System.currentTimeMillis();
            if (items != null) {
                for (NoticeStamp s : items) {
                    Date at = ContentUi.parseIso(s.PublishAt);
                    if (at != null && now - at.getTime() > RECENT_MS) seen.add(s.Id);
                }
            }
            editor.putBoolean(READY, true);
        } else {
            for (Long id : visible) if (!known.contains(id)) fresh.add(id);
        }
        // Keep only notices that are still visible, so the lists stay short
        seen.retainAll(visible);
        int unread = 0;
        for (Long id : visible) if (!seen.contains(id)) unread++;
        write(editor, SEEN, seen);
        write(editor, KNOWN, visible);
        editor.putInt(BADGE, unread);
        editor.apply();
        return new Result(unread, fresh);
    }

    /** Marks notices as seen (the student opened the notice list); returns the ones that were unseen before. */
    public static synchronized Set<Long> markSeen(Context context, Collection<Long> ids) {
        SharedPreferences prefs = prefs(context);
        Set<Long> seen = read(prefs, SEEN);
        Set<Long> known = read(prefs, KNOWN);
        Set<Long> before = new HashSet<>();
        for (Long id : ids) if (!seen.contains(id)) before.add(id);
        seen.addAll(ids);
        known.addAll(ids);
        SharedPreferences.Editor editor = prefs.edit();
        write(editor, SEEN, seen);
        write(editor, KNOWN, known);
        editor.putBoolean(READY, true);
        editor.putInt(BADGE, 0);
        editor.apply();
        return before;
    }

    /** Last known number of unread notices (shown until the next check). */
    public static int badge(Context context) {
        return prefs(context).getInt(BADGE, 0);
    }

    public static final class Result {
        public final int unread;
        public final List<Long> fresh;

        Result(int unread, List<Long> fresh) {
            this.unread = unread;
            this.fresh = fresh;
        }
    }
}
