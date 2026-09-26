package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/5/17.
 */

public class SignUpActivity extends BaseActivity {

    private static final String TAG = SignUpActivity.class.getName();
    private Context context = SignUpActivity.this;


    @BindView(R.id.webViewPayment)
    WebView webViewPayment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        LOG.e(TAG, "aaaa");
        context = this;


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SignUpActivity.this.setTitle(getString(R.string.lbl_sign_up));

        WebSettings webSettings = webViewPayment.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webViewPayment.clearCache(true);
        webViewPayment.clearHistory();
        webViewPayment.setWebViewClient(new WebViewClient());
        //http://114.134.93.150:2022/AnswerArea/MeritList?examId=77&userId=7d6dd3f8-4bee-4f03-ab43-cfd4e0b11b41
        String paymentUrl = RetrofitClient.BASE_URL+ "UserArea/AppUser/Registration";
        //LOG.e(TAG, paymentUrl);
        webViewPayment.loadUrl(paymentUrl);
    }
}
