package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ApiGenericResponse;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.PasswordUpdate;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.LOG;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/10/17.
 */

public class PasswordUpdateActivity extends BaseActivity {

    private static final String TAG = PasswordUpdateActivity.class.getName();
    private Context context = PasswordUpdateActivity.this;


    @BindView(R.id.toolbar)
    androidx.appcompat.widget.Toolbar toolbar;
    @BindView(R.id.input_password_old)
    EditText etPasswordOld;
    @BindView(R.id.input_password)
    EditText etPassword;
    @BindView(R.id.input_reEnterPassword)
    EditText etReEnterPassword;
    @BindView(R.id.btnPasswordUpdate)
    Button btnPasswordUpdate;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PasswordUpdateActivity.this.setTitle("Update Password");


        user = getPrefManager().getUserInfo();
        if (user == null) {
            showMsg("No User Found");
            finish();
        }

//        etPasswordOld.setEnabled(!isUserHasPassword());
        etPasswordOld.setVisibility(isUserHasPassword() ? View.GONE : View.VISIBLE);

        btnPasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

    }

    private void updatePassword() {
        if (!validate()) return;

        btnPasswordUpdate.setEnabled(false);
        AppUtils.hideKeyboard(context, this.getCurrentFocus());

        PasswordUpdate passwordUpdate = new PasswordUpdate();
        passwordUpdate.setId(user.getId());

        passwordUpdate.setNewPassword(etPassword.getText().toString());

        if (isUserHasPassword()) {
            RetrofitClient.getApiService(context).changePasswordForSocialUser(passwordUpdate).enqueue(new ApiCallback<ApiGenericResponse>(context,
                    new Callback<ApiGenericResponse>() {
                        @Override
                        public void onResponse(Call<ApiGenericResponse> call, Response<ApiGenericResponse> response) {
                            ApiGenericResponse apiGenericResponse = response.body();
                            afterGettingResponse(apiGenericResponse);
                        }

                        @Override
                        public void onFailure(Call<ApiGenericResponse> call, Throwable t) {
                        }
                    }));
        } else {
            passwordUpdate.setOldPassword(etPasswordOld.getText().toString());

            RetrofitClient.getApiService(context).changePassword(passwordUpdate).enqueue(new ApiCallback<ApiGenericResponse>(context,
                    new Callback<ApiGenericResponse>() {
                        @Override
                        public void onResponse(Call<ApiGenericResponse> call, Response<ApiGenericResponse> response) {
                            ApiGenericResponse apiGenericResponse = response.body();
                            afterGettingResponse(apiGenericResponse);
                        }

                        @Override
                        public void onFailure(Call<ApiGenericResponse> call, Throwable t) {
                        }
                    }));
        }
    }

    private void afterGettingResponse(ApiGenericResponse apiGenericResponse) {
        if (apiGenericResponse != null) {
            LOG.e(TAG, apiGenericResponse.toString());
            if (!apiGenericResponse.isIsError())
                onUpdateSuccess(apiGenericResponse);
            else
                onUpdateFailed(apiGenericResponse);
        } else
            onUpdateFailed(null);
    }

    private boolean isUserHasPassword() {
        return user.isHasPassword();
    }

    public void onUpdateSuccess(ApiGenericResponse apiGenericResponse) {
        btnPasswordUpdate.setEnabled(true);
        showMsg("Password Updated Successfully");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    public void onUpdateFailed(ApiGenericResponse apiGenericResponse) {
        if (apiGenericResponse != null)
            showMsg(apiGenericResponse.getMsg());
        btnPasswordUpdate.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String passwordOld = etPasswordOld.getText().toString();
        String password = etPassword.getText().toString();
        String reEnterPassword = etReEnterPassword.getText().toString();

        if (etPasswordOld.getVisibility() == View.VISIBLE) {
            if (passwordOld.isEmpty() || passwordOld.length() < 6) {
                etPasswordOld.setError(getString(R.string.err_password));
                valid = false;
            } else {
                etPasswordOld.setError(null);
            }
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError(getString(R.string.err_password));
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || !(reEnterPassword.equals(password))) {
            etReEnterPassword.setError(getString(R.string.err_password_re));
            valid = false;
        } else {
            etReEnterPassword.setError(null);
        }

        return valid;
    }
}
