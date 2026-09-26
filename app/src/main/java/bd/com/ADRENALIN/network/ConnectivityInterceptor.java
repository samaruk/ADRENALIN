package bd.com.ADRENALIN.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by mahfuz on 7/4/17.
 */

public class ConnectivityInterceptor implements Interceptor {

    private Context mContext;

    public ConnectivityInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!isNetworkActive(mContext)) {
            throw new NoConnectivityException();
        } else {
            Response response = chain.proceed(chain.request());
            return response;
        }
    }

    public boolean isNetworkActive(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public class NoConnectivityException extends IOException {

        @Override
        public String getMessage() {
            return "No network available, please check your WiFi or Data connection";
        }
    }
}
