package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.util.AppConstants;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/15/17.
 */

public class SplashActivity extends BaseActivity {

    public static final String TAG = SplashActivity.class.getSimpleName();
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        context = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, AppConstants.SPLASH_DURATION);
    }

}
