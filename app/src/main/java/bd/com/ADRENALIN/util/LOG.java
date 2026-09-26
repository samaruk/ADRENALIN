package bd.com.ADRENALIN.util;

import android.content.Context;
import android.provider.Settings;

import bd.com.ADRENALIN.BuildConfig;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ResponseJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/5/17.
 */

public class LOG {

    private static boolean isDebugMode() {
        return BuildConfig.DEBUG;
    }

    public static void i(String tag, String string) {
        if (isDebugMode()) android.util.Log.i(tag, string);
    }

    public static void e(String tag, String string) {
        if (isDebugMode()) android.util.Log.e(tag, string);
    }

    public static void d(String tag, String string) {
        if (isDebugMode()) android.util.Log.d(tag, string);
    }

    public static void v(String tag, String string) {
        if (isDebugMode()) android.util.Log.v(tag, string);
    }

    public static void w(String tag, String string) {
        if (isDebugMode()) android.util.Log.w(tag, string);
    }
    public static void SendToServer(Context context, String Message) {
        //http://deshnow.com/Home/SetLog?DeviceId=Testing&Message=Testing
        RetrofitClient.getApiService(context).SendErrorLog(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID),Message).enqueue(new ApiCallback<ResponseJson>(context,false,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {

                    }
                }));
    }
    public static void SendToServerWithName(Context context, String Message) {
        //http://deshnow.com/Home/SetLog?DeviceId=Testing&Message=Testing
        RetrofitClient.getApiService(context).SendErrorLog(android.os.Build.MODEL+"  |  "+Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID),Message).enqueue(new ApiCallback<ResponseJson>(context,false,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {

                    }
                }));
    }
}