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

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.RegistrationOptions;
import bd.com.ADRENALIN.pojo.RegistrationRequest;
import bd.com.ADRENALIN.pojo.ResponseJson;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Native sign-up screen. Posts to the same endpoint as the web registration page
 * (UserArea/AppUser/AddAppUser), so accounts are created exactly as before.
 *
 * Fields are laid out two per row (Name and Address use the full width). The candidate type,
 * medical college, faculty, subject, batch and remarks are not asked here: the admin sets them
 * on the App Users page.
 *
 * Created by mahfuz on 7/5/17.
 */
public class SignUpActivity extends BaseActivity {

    private static final String TAG = SignUpActivity.class.getName();
    private Context context = SignUpActivity.this;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_name)
    EditText etName;
    @BindView(R.id.input_email)
    EditText etEmail;
    @BindView(R.id.input_phone)
    EditText etPhone;
    @BindView(R.id.input_address)
    EditText etAddress;
    @BindView(R.id.input_password)
    EditText etPassword;
    @BindView(R.id.input_password_re)
    EditText etPasswordRe;
    @BindView(R.id.btnRegister)
    AppCompatButton btnRegister;
    @BindView(R.id.tvSignUpError)
    TextView tvError;

    private RegistrationOptions options;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        context = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SignUpActivity.this.setTitle(getString(R.string.lbl_sign_up));

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        loadOptions();
    }

    /**
     * Loads the message shown after signing up (it carries the office phone number). The lists in the
     * reply are only for older app versions. Without it the form still works and a plain message is shown.
     */
    private void loadOptions() {
        RetrofitClient.getApiService(context).getRegistrationOptions().enqueue(new ApiCallback<RegistrationOptions>(context,
                new Callback<RegistrationOptions>() {
                    @Override
                    public void onResponse(Call<RegistrationOptions> call, Response<RegistrationOptions> response) {
                        RegistrationOptions result = response.body();
                        if (result != null && !result.isError()) options = result;
                    }

                    @Override
                    public void onFailure(Call<RegistrationOptions> call, Throwable t) {
                    }
                }));
    }

    /**
     * Checks the form. Name and password are required, and the email or the mobile number (at least one
     * of them; each one given must be valid); the address is optional. The first problem is also shown
     * above the form and focused.
     */
    private boolean validate() {
        tvError.setVisibility(View.GONE);
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String passwordRe = etPasswordRe.getText().toString();
        String phone = etPhone.getText().toString().trim();

        String firstError = null;
        EditText firstField = null;

        String error = name.isEmpty() ? getString(R.string.err_name_required) : null;
        etName.setError(error);
        if (error != null && firstError == null) { firstError = error; firstField = etName; }

        if (email.isEmpty() && phone.isEmpty()) {
            // Either one is enough, but one of them is needed
            error = getString(R.string.err_email_or_mobile_required);
            etEmail.setError(error);
            etPhone.setError(error);
            if (firstError == null) { firstError = error; firstField = etEmail; }
        } else {
            error = email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ? null : getString(R.string.err_email);
            etEmail.setError(error);
            if (error != null && firstError == null) { firstError = error; firstField = etEmail; }

            error = phone.isEmpty() || phone.matches("[0-9]{8,15}") ? null : getString(R.string.err_phone_digits);
            etPhone.setError(error);
            if (error != null && firstError == null) { firstError = error; firstField = etPhone; }
        }

        // Password errors carry no icon so the show/hide eye stays visible
        error = password.length() < 6 ? getString(R.string.err_password) : null;
        etPassword.setError(error, null);
        if (error != null && firstError == null) { firstError = error; firstField = etPassword; }

        error = password.equals(passwordRe) ? null : getString(R.string.err_password_re);
        etPasswordRe.setError(error, null);
        if (error != null && firstError == null) { firstError = error; firstField = etPasswordRe; }

        if (firstError != null) {
            tvError.setText(firstError);
            tvError.setVisibility(View.VISIBLE);
            firstField.requestFocus();
            return false;
        }
        return true;
    }

    private void register() {
        if (!validate()) return;
        btnRegister.setEnabled(false);
        RegistrationRequest request = new RegistrationRequest();
        request.name = etName.getText().toString().trim();
        request.email = etEmail.getText().toString().trim();
        request.phone = etPhone.getText().toString().trim();
        request.address = etAddress.getText().toString().trim();
        request.password = etPassword.getText().toString();

        RetrofitClient.getApiService(context).registerAppUser(request).enqueue(new ApiCallback<ResponseJson>(context,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        btnRegister.setEnabled(true);
                        ResponseJson result = response.body();
                        if (result == null) {
                            showError(getString(R.string.err_server));
                            return;
                        }
                        if (result.getIsError()) {
                            showError(result.getMsg() != null ? result.getMsg() : getString(R.string.err_server));
                            return;
                        }
                        onRegistered();
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        btnRegister.setEnabled(true);
                        showError(getString(R.string.err_network));
                    }
                }));
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        showMsg(message);
    }

    private void onRegistered() {
        String message = options != null && options.getSuccessMessage() != null
                ? options.getSuccessMessage()
                : getString(R.string.lbl_registration_success);
        new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                .setTitle(getString(R.string.lbl_sign_up))
                .setMessage(message)
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
}
