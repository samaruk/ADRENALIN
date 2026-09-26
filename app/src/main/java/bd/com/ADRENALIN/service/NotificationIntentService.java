package bd.com.ADRENALIN.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.LOG;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/14/17.
 */

public class NotificationIntentService extends IntentService {

    private Context context;
    public static final String TAG = NotificationIntentService.class.getSimpleName();
    public static final String NOTIFICATION_MESSAGES_INTENT_CODE = "NOTIFICATION_MESSAGES_INTENT_CODE ";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        context = this;

        LOG.e(TAG, "Helllaaaa from Intent Service at " + AppUtils.getTimeStringFromDate(Calendar.getInstance().getTime()));

//        String dummyResponse = "[\"Testing - 1\",\"Testing - 2\",\"Testing - 3\"]";
//
//        List<String> notificationList = new Gson().fromJson(dummyResponse, new TypeToken<List<String>>() {
//        }.getType());

        if (intent != null && !intent.getStringExtra(AppConstants.NotificationConstants.USER_ID).isEmpty()) {
////        RetrofitClient.getApiService(context).getNotifications("03/07/2017 18:11").enqueue(new ApiCallback<NotificationConstants>(context,
            RetrofitClient.getApiService(context)
                    .getNotifications(intent.getStringExtra(AppConstants.NotificationConstants.USER_ID))
                    .enqueue(new ApiCallback<List<String>>(context, false,
                            new Callback<List<String>>() {
                                @Override
                                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                                    List<String> notificationList = response.body();
                                    LOG.e(TAG, notificationList.toString());
                                    sendNotification(notificationList);
                                }

                                @Override
                                public void onFailure(Call<List<String>> call, Throwable t) {
                                }
                            }));
        }


    }

    private void sendNotification(List<String> notificationList) {
        if (notificationList == null && notificationList.isEmpty()) {
            return;
        }

        for (String msg : notificationList) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_action_notification)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(msg);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setColor(ContextCompat.getColor(context, R.color.primary_dark));
            }

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // When you issue multiple notifications about the same type of event, it’s best practice for your app
            // to try to update an existing notification with this new information, rather than immediately creating a new notification.
            // If you want to update this notification at a later date, you need to assign it an ID.
            // You can then use this ID whenever you issue a subsequent notification.
            // If the previous notification is still visible, the system will update this existing notification,
            // rather than create a new one. In this example, the notification’s ID is 001//

            //        NotificationManager.notify().

            mNotificationManager.notify(new Random().nextInt(10000000), mBuilder.build());
        }

//*******************************************
//*******************************************
//        Intent resultIntent = new Intent(this, MainActivity.class);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
//        PendingIntent resultPendingIntent =
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        resultIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
    }
}
