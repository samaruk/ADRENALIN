package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ApiGenericResponse;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.util.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/15/17.
 */

public class PaymentActivityForArchive extends BaseActivity {

    public static final String TAG = PaymentActivityForArchive.class.getSimpleName();
    private Context context;

    @BindView(R.id.webViewPayment)
    WebView webViewPayment;

    int examId,categoryId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        context = this;

        Intent intent = getIntent();
        if (intent == null) return;

//        intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, archive.getId());
//        intent.putExtra(AppConstants.ExamConstants.INTENT_FROM_ARCHIVE, fromArchive);

        examId = intent.getIntExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, 0);
        categoryId=intent.getIntExtra(AppConstants.ExamConstants.INTENT_EXAM_CATEGORY, 0);
//        String examString = intent.getStringExtra(AppConstants.ExamConstants.INTENT_CODE);
        if (examId != 0) {

            /** Dummy Exam ID*/
//            examId = 1;

            WebSettings webSettings = webViewPayment.getSettings();
            webSettings.setJavaScriptEnabled(true);

            CookieManager.getInstance().setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webViewPayment, true);
            }

            webViewPayment.clearCache(true);
            webViewPayment.clearHistory();

            webViewPayment.addJavascriptInterface(new WebAppInterface(context), "android");

            String paymentUrl = RetrofitClient.getPaymantUrl(getPrefManager().getUserInfo().getId(), examId);

//            LOG.e(TAG, paymentUrl);
            webViewPayment.loadUrl(paymentUrl);
        }
    }


    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void paymentCallback(String response) {
//            LOG.e(TAG, response);
            checkPaymentResponse(response);
        }
    }

    private void checkPaymentResponse(String response) {
        if (response != null && !response.isEmpty()) {
            ApiGenericResponse apiGenericResponse = new Gson().fromJson(response, ApiGenericResponse.class);
//            ApiGenericResponse apiGenericResponse = new ApiGenericResponse()
//                    .withIsError(true);
            if (apiGenericResponse != null) {
                if (!apiGenericResponse.isIsError()) {
                    paymentSuccess(apiGenericResponse.getMsg());
                } else {
                    paymentFailed(apiGenericResponse.getMsg());
                }
            }
        } else {
            paymentFailed(null);
        }
    }


    private void paymentSuccess(String msg) {
        if (msg == null || msg.isEmpty())
            msg = getString(R.string.lbl_payment_success);

        final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog).setMessage(msg).setCancelable(false).create();

        alertDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                alertDialog.dismiss();

                Intent intent = new Intent(context, ExamAnswerActivity.class);
                intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, examId);
                intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_CATEGORY, categoryId);
                intent.putExtra(AppConstants.ExamConstants.INTENT_FROM_ARCHIVE, true);
//                    intent.putExtra(AppConstants.ExamConstants.INTENT_CODE, new Gson().toJson(examInfo));
                startActivity(intent);
                AppUtils.startActivityAnimation(context);
                finish();

            }
        }, AppConstants.PaymentConstants.CALLBACK_REDIRECT_INTERVAL);
    }

    private void paymentFailed(String msg) {
        if (msg == null || msg.isEmpty())
            msg = getString(R.string.lbl_payment_failed);

        final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                .setMessage(msg)
                .setCancelable(false)
                .create();

        alertDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                finish();
            }
        }, AppConstants.PaymentConstants.CALLBACK_REDIRECT_INTERVAL);
    }
}
