package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import bd.com.ADRENALIN.pojo.User;

/**
 * Created by iqrasys on 9/9/2017.
 */

public class NotificationEventActivity extends BaseActivity {

    public static final String TAG = AppReviewActivity.class.getSimpleName();
    private Context context;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        user = getPrefManager().getUserInfo();

    }
}
