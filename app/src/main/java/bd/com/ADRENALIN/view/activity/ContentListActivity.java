package bd.com.ADRENALIN.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.User;

/**
 * A toolbar and a list with loading, empty and error states (layout activity_content_list):
 * the base of All Courses, Course Plan, Lectures and Notices.
 */
public abstract class ContentListActivity extends BaseActivity {

    protected RecyclerView list;
    private View state;
    private ProgressBar progress;
    private TextView message;

    /** Loads (or reloads) the list; called on start and when the student taps an error message. */
    protected abstract void load();

    protected void onCreate(@Nullable Bundle savedInstanceState, String title) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);
        list = findViewById(R.id.content_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        state = findViewById(R.id.content_state);
        progress = findViewById(R.id.content_progress);
        message = findViewById(R.id.content_message);
        load();
    }

    protected void showLoading() {
        state.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        message.setText(R.string.lbl_loading);
        state.setOnClickListener(null);
    }

    /** A message in the middle of the screen (empty list); with retry the student can tap it to load again. */
    protected void showMessage(String text, boolean retry) {
        state.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        message.setText(retry ? text + "\n\n" + getString(R.string.lbl_retry) : text);
        if (retry) {
            state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoading();
                    load();
                }
            });
        } else {
            state.setOnClickListener(null);
        }
    }

    protected void showList() {
        state.setVisibility(View.GONE);
    }

    /** Signed-in student's id (null when not signed in). */
    protected String userId() {
        User user = getPrefManager().getUserInfo();
        return user != null ? user.getId() : null;
    }

    protected String errorText(String serverMessage) {
        return serverMessage != null && serverMessage.length() > 0 ? serverMessage : getString(R.string.err_server);
    }
}
