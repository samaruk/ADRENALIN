package bd.com.ADRENALIN.service;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import bd.com.ADRENALIN.pojo.DeviceModel;
import bd.com.ADRENALIN.util.PrefManager;

/**
 * Created by iqrasys on 8/26/2017.
 */

public class AppFirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private PrefManager prefManager;
    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        try {
            Context ctx = getApplicationContext();
            prefManager = new PrefManager(ctx);
            if(prefManager.getUserInfo()!=null)
            DeviceModel.Save(ctx);
            else {
                Log.e("SamarukWebSocket", "Error in  onTokenRefresh : User Is Null." );
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket", "Error in  onTokenRefresh : " + e.getMessage());
            if(prefManager!=null){
                prefManager.setIsDeviceSet(-1);
            }
        }
    }
}
