package bd.com.ADRENALIN.pojo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.DevicePostModel;
import bd.com.ADRENALIN.util.PrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iqrasys on 9/9/2017.
 */

public class DeviceModel {

    public static final void Save(Context context, String key, String deviceId, String deviceModel){
        final PrefManager prefManager=new PrefManager(context);
        DevicePostModel model=new DevicePostModel();
        prefManager.setIsDeviceSet(0);
        model.DeviceId=deviceId;
        model.Name = deviceModel;
        model.NotificationKey=key;
        model.UserId=prefManager.getUserInfo().getId();

        RetrofitClient.getApiService(context).AddDevice(model).enqueue(new ApiCallback<ResponseJson>(context,false,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson result = response.body();
                        if (result == null || result.IsError){
                            prefManager.setIsDeviceSet(1);
                        }else {
                            prefManager.setIsDeviceSet(-1);
                            Log.e("SamarukWebSocket", "AddDevice Successfull. " );
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        prefManager.setIsDeviceSet(-1);
                    }
                }));

    }

    public static final void Save(Context context){
        try {
            if(HasConnection(context)){
                String deviseId= Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                String key= FirebaseInstanceId.getInstance().getToken();
                Save(context,key,deviseId,android.os.Build.MODEL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket", "AddDevice  Error. "+e.getMessage() );
        }
    }

    public static final boolean HasConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return  activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
