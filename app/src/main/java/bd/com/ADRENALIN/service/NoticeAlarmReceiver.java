package bd.com.ADRENALIN.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bd.com.ADRENALIN.util.NoticeChecker;

/** Runs the background notice check, and schedules it again after the phone restarts. */
public class NoticeAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final Context app = context.getApplicationContext();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            NoticeChecker.schedule(app);
            return;
        }
        final PendingResult pending = goAsync();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NoticeChecker.checkNow(app);
                } finally {
                    pending.finish();
                }
            }
        }).start();
    }
}
