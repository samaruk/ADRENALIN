package bd.com.ADRENALIN.network;


import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import bd.com.ADRENALIN.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/4/17.
 */

public class ApiCallback<T> implements Callback<T> {

    private Context context;
    private final Callback<T> mCallback;
    private ProgressDialog progressDialog;

    public ApiCallback(Context context, Callback<T> callback) {
        this.context = context;
        this.mCallback = callback;

        initProgressDialog(context);
        showProgressDialog();
    }

    /**
     * Callback with choice of not including Progress Dialog. Ideal for fetching data background
     *
     * @param context
     * @param callback
     */
    public ApiCallback(Context context, boolean showProgressBar, Callback<T> callback) {
        this.context = context;
        this.mCallback = callback;

        if (showProgressBar) {
            initProgressDialog(context);
            showProgressDialog();
        }
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        hideProgressDialog();
        if (response.isSuccessful())
            mCallback.onResponse(call, response);
        else {
            int statusCode = response.code();
            // handle request errors depending on status code
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        hideProgressDialog();
        boolean isErrorFound = false;
        if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

            isErrorFound = true;
        }

        if (isErrorFound) return;

        // This won't call if general error (like above) occurred. Those should be handled in general.
        mCallback.onFailure(call, t);
    }

    private void initProgressDialog(Context context) {
//        progressDialog = new ProgressDialog(context);
        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }

    private void showProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                hideProgressDialog();

            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
