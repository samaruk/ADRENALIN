package bd.com.ADRENALIN.view.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.util.AppConstants;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by IqraAdmin on 11/2/2020.
 */

public class MeritListActivity extends BaseActivity {
    public static final String TAG = MeritListActivity.class.getSimpleName();
    private MeritListActivity context;
    private int examId = -1;
    @BindView(R.id.webViewPayment)
    WebView webViewPayment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merit_list);
        ButterKnife.bind(this);
        LOG.e(TAG, "aaaa");
        context = this;


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MeritListActivity.this.setTitle(getString(R.string.lbl_merit_list));

        Intent intent = getIntent();
        if (intent == null) return;

        //LOG.e(TAG, "bbb");
        if (intent != null) {
            examId=intent.getIntExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, 0);
        }
        WebSettings webSettings = webViewPayment.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webViewPayment.clearCache(true);
        webViewPayment.clearHistory();
        webViewPayment.setWebViewClient(new WebViewClient());
        //http://114.134.93.150:2022/AnswerArea/MeritList?examId=77&userId=7d6dd3f8-4bee-4f03-ab43-cfd4e0b11b41
            String paymentUrl = RetrofitClient.BASE_URL+ "AnswerArea/MeritList?examId="+examId+"&userId="+getPrefManager().getUserInfo().getId();
            //LOG.e(TAG, paymentUrl);
            webViewPayment.loadUrl(paymentUrl);
    }
}

