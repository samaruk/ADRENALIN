package bd.com.ADRENALIN.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.content.NoticeSummaryResponse;
import bd.com.ADRENALIN.service.NoticeAlarmReceiver;
import bd.com.ADRENALIN.view.activity.NoticeActivity;

/**
 * New notices: a repeating background check (every half hour, when the phone is awake) posts a notification
 * with sound; while the app is open MainActivity checks every minute and rings in the app.
 */
public final class NoticeChecker {

    public static final String CHANNEL = "notices";
    public static final String ACTION_CHECK = "bd.com.ADRENALIN.CHECK_NOTICES";
    private static final int NOTIFICATION_ID = 7001;

    private NoticeChecker() {
    }

    private static int immutable() {
        return Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0;
    }

    /** Starts (or keeps) the background check. Safe to call often. */
    public static void schedule(Context context) {
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarms == null) return;
        Intent intent = new Intent(context, NoticeAlarmReceiver.class).setAction(ACTION_CHECK);
        PendingIntent pending = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | immutable());
        alarms.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_HALF_HOUR, pending);
    }

    public static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT < 26) return;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null || manager.getNotificationChannel(CHANNEL) != null) return;
        NotificationChannel channel = new NotificationChannel(CHANNEL, context.getString(R.string.lbl_notice_channel), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(context.getString(R.string.lbl_notice_channel_desc));
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
        manager.createNotificationChannel(channel);
    }

    /** The notification sound, played in the app when a new notice arrives while it is open. */
    public static void playSound(Context context) {
        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), uri);
            if (ringtone != null) ringtone.play();
        } catch (Exception ignored) {
        }
    }

    /** A notification (with sound) that opens the notice list. */
    public static void notifyNew(Context context, int count, String latestTitle) {
        ensureChannel(context);
        Intent open = new Intent(context, NoticeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pending = PendingIntent.getActivity(context, 2, open, PendingIntent.FLAG_UPDATE_CURRENT | immutable());
        String title = count > 1 ? context.getString(R.string.lbl_new_notices, count) : context.getString(R.string.lbl_new_notice);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL)
                .setSmallIcon(R.drawable.ic_cc_bell)
                .setContentTitle(title)
                .setContentText(TextUtils.isEmpty(latestTitle) ? context.getString(R.string.app_name) : latestTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(latestTitle))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pending);
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException ignored) {
            // notifications not allowed (Android 13+ permission refused)
        }
    }

    /** Background check (runs off the main thread): asks the server and notifies about notices that did not ring yet. */
    public static void checkNow(Context context) {
        if (new PrefManager(context).getUserInfo() == null) return;
        try {
            NoticeSummaryResponse summary = RetrofitClient.getApiService(context).getNoticeSummary().execute().body();
            if (summary == null || summary.IsError) return;
            NoticeState.Result result = NoticeState.update(context, summary.Items);
            if (!result.fresh.isEmpty()) notifyNew(context, result.fresh.size(), summary.LatestTitle);
        } catch (Exception ignored) {
            // offline or server unreachable: try again next time
        }
    }
}
