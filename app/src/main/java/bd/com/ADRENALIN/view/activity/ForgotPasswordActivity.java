package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ResponseJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Forgot password: step 1 sends a code to the account's email, step 2 sets a new
 * password with that code.
 */
public class ForgotPasswordActivity extends BaseActivity {

    private Context context;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_email)
    EditText etEmail;
    @BindView(R.id.btnSendCode)
    AppCompatButton btnSendCode;
    @BindView(R.id.llStep2)
    View llStep2;
    @BindView(R.id.tvCodeInfo)
    TextView tvCodeInfo;
    @BindView(R.id.input_code)
    EditText etCode;
    @BindView(R.id.input_password)
    EditText etPassword;
    @BindView(R.id.input_password_re)
    EditText etPasswordRe;
    @BindView(R.id.btnReset)
    AppCompatButton btnReset;
    @BindView(R.id.tvResend)
    TextView tvResend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.lbl_forgot_password));

        String prefill = getIntent() != null ? getIntent().getStringExtra("email") : null;
        if (prefill != null) etEmail.setText(prefill);

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private String emailOrPhone() {
        return etEmail.getText().toString().trim();
    }

    private void sendCode() {
        if (emailOrPhone().isEmpty()) {
            etEmail.setError(getString(R.string.err_email_or_phone));
            return;
        }
        etEmail.setError(null);
        btnSendCode.setEnabled(false);
        Map<String, String> body = new HashMap<>();
        body.put("Email", emailOrPhone());
        RetrofitClient.getApiService(context).forgotPassword(body).enqueue(new ApiCallback<ResponseJson>(context,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        btnSendCode.setEnabled(true);
                        ResponseJson result = response.body();
                        if (result == null) {
                            showMsg(getString(R.string.err_server));
                            return;
                        }
                        if (result.getIsError()) {
                            showMsg(result.getMsg() != null ? result.getMsg() : getString(R.string.err_server));
                            return;
                        }
                        tvCodeInfo.setText(result.getMsg() != null ? result.getMsg() : getString(R.string.lbl_code_sent));
                        llStep2.setVisibility(View.VISIBLE);
                        etCode.requestFocus();
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        btnSendCode.setEnabled(true);
                        showMsg(getString(R.string.err_network));
                    }
                }));
    }

    private void resetPassword() {
        String code = etCode.getText().toString().trim();
        String password = etPassword.getText().toString();
        String passwordRe = etPasswordRe.getText().toString();
        boolean valid = true;
        if (code.isEmpty()) {
            etCode.setError(getString(R.string.err_code));
            valid = false;
        } else {
            etCode.setError(null);
        }
        if (password.length() < 6) {
            etPassword.setError(getString(R.string.err_password));
            valid = false;
        } else {
            etPassword.setError(null);
        }
        if (!password.equals(passwordRe)) {
            etPasswordRe.setError(getString(R.string.err_password_re));
            valid = false;
        } else {
            etPasswordRe.setError(null);
        }
        if (!valid) return;

        btnReset.setEnabled(false);
        Map<String, String> body = new HashMap<>();
        body.put("Email", emailOrPhone());
        body.put("Otp", code);
        body.put("NewPassword", password);
        RetrofitClient.getApiService(context).resetPassword(body).enqueue(new ApiCallback<ResponseJson>(context,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        btnReset.setEnabled(true);
                        ResponseJson result = response.body();
                        if (result == null) {
                            showMsg(getString(R.string.err_server));
                            return;
                        }
                        if (result.getIsError()) {
                            showMsg(result.getMsg() != null ? result.getMsg() : getString(R.string.err_server));
                            return;
                        }
                        new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                                .setMessage(result.getMsg() != null ? result.getMsg() : getString(R.string.lbl_password_reset_done))
                                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        btnReset.setEnabled(true);
                        showMsg(getString(R.string.err_network));
                    }
                }));
    }
}
