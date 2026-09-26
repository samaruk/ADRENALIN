package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.DeviceModel;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/5/17.
 */

public class SignInActivity extends BaseActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();
    private Context context;

    @BindView(R.id.input_email)
    EditText etEmail;
    @BindView(R.id.input_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.link_signup)
    TextView txtSignUpLink;
    @BindView(R.id.cbRememberMe)
    CheckBox cbRememberMe;


    private CallbackManager callbackManager;

    private int RC_SIGN_IN = 499;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        context = SignInActivity.this;

        if (isUserAlreadySignedIn()) {
            gotoHome(false);
            return;
        }

        setButtonListeners();
    }

    private void setButtonListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        txtSignUpLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                startActivity(new Intent(context, SignUpActivity.class));
                AppUtils.startActivityAnimation(context);
            }
        });
    }
    public void login() {
        LOG.e("LogInCalled", "Email:" +etEmail.getText().toString());
        if (!validate()) return;

        LOG.e("LogInCalled11", "Email:" +etEmail.getText().toString());
        btnLogin.setEnabled(false);

//        User user = new User().withEmail("samaruk09@gmail.com").withPassword("asdf1234");
        User user = new User()
                .withEmail(etEmail.getText().toString())
                .withPassword(etPassword.getText().toString());

        LOG.e("LogInCalled11", "Email:" +etEmail.getText().toString());

        RetrofitClient.getApiService(context).userLogin(user).enqueue(new ApiCallback<User>(context,
                new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user1 = response.body();
                        if (user1 != null && !user1.isIsError())
                            onLoginSuccess(user1);
                        else
                            onLoginFailed(user1 != null ? user1.getMsg() : getString(R.string.err_server));

                        LOG.e(TAG, user1.toString());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                }));

    }

    public void onLoginSuccess(User user) {
        if (cbRememberMe.isChecked())
            getPrefManager().setRememberMe(true);

        btnLogin.setEnabled(true);

        storeUserToPref(user);
        gotoHome(true);
    }

    private void gotoHome(boolean isLogedIn) {
        if(isLogedIn){
            DeviceModel.Save(context);
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(String msg) {
        showMsg(msg);
        btnLogin.setEnabled(true);
    }

    private void storeUserToPref(User user) {
        getPrefManager().setUserInfo(user);
    }

    private boolean isUserAlreadySignedIn() {
        return getPrefManager().isRememberMe();
    }

    public boolean validate() {
        boolean valid = true;

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        LOG.e("LogInCalled33", "Email:" +email+", password:"+password);
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.err_email));
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError(getString(R.string.err_password));
            valid = false;
        } else {
            etPassword.setError(null);
        }

        LOG.e("LogInCalled22", "Email:" +valid);
        return valid;
    }
}
