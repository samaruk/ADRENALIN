package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import com.google.android.material.textfield.TextInputLayout;
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
import bd.com.ADRENALIN.pojo.content.PasswordResetConfig;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Forgot password: step 1 sends a code to the account's email or mobile, step 2 sets a new
 * password with that code. What the screen asks for follows the Otp.Delivery setting of the server:
 * Email (email address only), Sms (mobile number only) or Both (either).
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
    @BindView(R.id.tvForgotHelp)
    TextView tvHelp;
    @BindView(R.id.tilForgotAccount)
    TextInputLayout tilAccount;

    private static final String DELIVERY_EMAIL = "Email", DELIVERY_SMS = "Sms", DELIVERY_BOTH = "Both";
    /** Last delivery the server sent, so the screen does not change while the setting loads. */
    private static final String PREFS = "adrenalin_app", KEY_DELIVERY = "otp_delivery";
    private String delivery = DELIVERY_EMAIL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.lbl_forgot_password));

        applyDelivery(getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_DELIVERY, DELIVERY_EMAIL));
        loadDelivery();

        String prefill = getIntent() != null ? getIntent().getStringExtra("email") : null;
        if (prefill != null && !(DELIVERY_SMS.equals(delivery) && prefill.contains("@"))) etEmail.setText(prefill);

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

    /** Text, hint and keyboard for the delivery set on the server. */
    private void applyDelivery(String value) {
        if (DELIVERY_SMS.equalsIgnoreCase(value)) {
            delivery = DELIVERY_SMS;
            tvHelp.setText(R.string.lbl_forgot_help_sms);
            tilAccount.setHint(getString(R.string.hint_forgot_mobile));
            etEmail.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (DELIVERY_BOTH.equalsIgnoreCase(value)) {
            delivery = DELIVERY_BOTH;
            tvHelp.setText(R.string.lbl_forgot_help_both);
            tilAccount.setHint(getString(R.string.hint_forgot_both));
            etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else {
            delivery = DELIVERY_EMAIL;
            tvHelp.setText(R.string.lbl_forgot_help_email);
            tilAccount.setHint(getString(R.string.hint_forgot_email));
            etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        etEmail.setError(null);
    }

    private void loadDelivery() {
        RetrofitClient.getApiService(context).getPasswordResetConfig().enqueue(new Callback<PasswordResetConfig>() {
            @Override
            public void onResponse(Call<PasswordResetConfig> call, Response<PasswordResetConfig> response) {
                PasswordResetConfig config = response.body();
                if (config == null || config.IsError || config.Delivery == null || isFinishing()) return;
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_DELIVERY, config.Delivery).apply();
                if (!config.Delivery.equalsIgnoreCase(delivery)) applyDelivery(config.Delivery);
            }

            @Override
            public void onFailure(Call<PasswordResetConfig> call, Throwable t) {
            }
        });
    }

    /** Error message when the typed account does not fit the delivery, otherwise null. */
    private String accountError(String value) {
        if (value.isEmpty()) {
            return getString(DELIVERY_SMS.equals(delivery) ? R.string.err_forgot_mobile
                    : DELIVERY_EMAIL.equals(delivery) ? R.string.err_forgot_email : R.string.err_email_or_phone);
        }
        if (DELIVERY_EMAIL.equals(delivery) && !Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            return getString(R.string.err_forgot_email);
        }
        if (DELIVERY_SMS.equals(delivery) && !value.replaceAll("[\\s-]", "").matches("\\+?[0-9]{8,15}")) {
            return getString(R.string.err_forgot_mobile);
        }
        return null;
    }

    private void sendCode() {
        String error = accountError(emailOrPhone());
        if (error != null) {
            etEmail.setError(error);
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
