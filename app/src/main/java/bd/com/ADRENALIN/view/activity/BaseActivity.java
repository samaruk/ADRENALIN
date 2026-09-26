package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.util.PrefManager;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 6/24/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private PrefManager prefManager;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = BaseActivity.this;
        prefManager = new PrefManager(context);
    }

    public PrefManager getPrefManager() {
        return prefManager;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    public void showMsg(String msg) {
        showMsg(msg, Snackbar.LENGTH_LONG);
    }

    public void showMsg(String msg, int duration) {
//        Snackbar.make(findViewById(android.R.id.content), msg, duration).show();
        // create instance
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, duration);

        // get snackbar view
        View snackbarView = snackbar.getView();

        // change snackbar text color
        int snackbarTextId = com.google.android.material.R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));

        // change snackbar background
//        snackbarView.setBackgroundColor(Color.MAGENTA);
        snackbar.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
//                overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
