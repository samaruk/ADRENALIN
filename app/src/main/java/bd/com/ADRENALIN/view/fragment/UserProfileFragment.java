package bd.com.ADRENALIN.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ApiGenericResponse;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.view.activity.PasswordUpdateActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/10/17.
 */

public class UserProfileFragment extends BaseFragment {

    private static final String TAG = UserProfileFragment.class.getName();
    private Context context;

    @BindView(R.id.input_name)
    EditText etName;
    @BindView(R.id.input_email)
    EditText etEmail;
    @BindView(R.id.input_phone)
    EditText etPhone;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.btn_update_password)
    Button btnUpdatePassword;
    @BindView(R.id.rbMale)
    RadioButton rbMale;
    @BindView(R.id.rbFemale)
    RadioButton rbFemale;

    User userToRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        //TODO:
        // Set existing user data to UI
        // See if update works


        setExistingUserDataToUI();
    }

    public void setExistingUserDataToUI() {
        User user = getPrefManager().getUserInfo();
        LOG.e(TAG, user.toString());
        if (user != null) {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            if (user.getGenderId() == 1)
                rbMale.setChecked(true);
            else
                rbFemale.setChecked(true);

        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PasswordUpdateActivity.class));
                AppUtils.startActivityAnimation(getActivity());
            }
        });
    }

    public void updateUserProfile() {
        if (!validate()) return;

        btnUpdate.setEnabled(false);

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();
        int gender = rbMale.isChecked() ? 1 : 2;

        userToRegister = new User().withId(getPrefManager().getUserInfo().getId())
                .withName(name).withEmail(email)
                .withPhone(phone).withGenderId(gender);


        RetrofitClient.getApiService(context).userUpdate(userToRegister).enqueue(new ApiCallback<ApiGenericResponse>(context,
                new Callback<ApiGenericResponse>() {
                    @Override
                    public void onResponse(Call<ApiGenericResponse> call, Response<ApiGenericResponse> response) {
                        ApiGenericResponse apiGenericResponse = response.body();
                        if (apiGenericResponse != null) {
//                            LOG.e(TAG, apiGenericResponse.toString());
                            if (!apiGenericResponse.isIsError())
                                onUpdateSuccess(apiGenericResponse);
                            else
                                onUpdateFailed(apiGenericResponse);
                        } else
                            onUpdateFailed(null);
                    }

                    @Override
                    public void onFailure(Call<ApiGenericResponse> call, Throwable t) {
                    }
                }));
    }

    public void onUpdateSuccess(ApiGenericResponse apiGenericResponse) {
        btnUpdate.setEnabled(true);
        storeUserToPref(userToRegister);
        showMsg("Profile Updated Successfully");
    }

    private void storeUserToPref(User user) {
        getPrefManager().setUserInfo(user);
    }

    public void onUpdateFailed(ApiGenericResponse apiGenericResponse) {
        if (apiGenericResponse != null)
            showMsg(apiGenericResponse.getMsg());
        btnUpdate.setEnabled(true);
    }


    public boolean validate() {
        boolean valid = true;

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            etName.setError(getString(R.string.err_name));
            valid = false;
        } else {
            etName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.err_email));
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (phone.isEmpty() || phone.length() != 11) {
            etPhone.setError(getString(R.string.err_phone));
            valid = false;
        } else {
            etPhone.setError(null);
        }

//        if (password.isEmpty() || password.length() < 6) {
//            etPassword.setError(getString(R.string.err_password));
//            valid = false;
//        } else {
//            etPassword.setError(null);
//        }
//
//        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || !(reEnterPassword.equals(password))) {
//            etReEnterPassword.setError(getString(R.string.err_password_re));
//            valid = false;
//        } else {
//            etReEnterPassword.setError(null);
//        }

        return valid;
    }

}
