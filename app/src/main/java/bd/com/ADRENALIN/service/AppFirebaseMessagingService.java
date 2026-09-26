package bd.com.ADRENALIN.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.view.activity.MainActivity;
import bd.com.ADRENALIN.util.AppConstants;

/**
 * Created by iqrasys on 8/26/2017.
 */

public class AppFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //It is optional
        try {
            sendNotification(remoteMessage.getData());
            Log.e("SamarukWebSocket", "EVENT_Data is "+new Gson().toJson(remoteMessage.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket", "onMessageReceived Error: " + e.getMessage());
        }
    }

    //This method is only generating push notification
    public void sendNotification(Map<String,String> messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppConstants.NotificatioEvent.EVENT_ID, Integer.parseInt(messageBody.get("Id")));
        Log.e("SamarukWebSocket", "EVENT_ID Is "+Integer.parseInt(messageBody.get("Id")));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageBody.get("Title"))
                .setContentText(messageBody.get("Content"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
