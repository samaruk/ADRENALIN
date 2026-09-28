package bd.com.ADRENALIN.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.content.NoticeItem;
import bd.com.ADRENALIN.util.ContentUi;
import bd.com.ADRENALIN.view.adapter.content.PhotoStripAdapter;

/** One notice with its pictures and attachments. */
public class NoticeDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.lbl_notice));

        NoticeItem n = new Gson().fromJson(getIntent().getStringExtra(NoticeActivity.EXTRA_NOTICE), NoticeItem.class);
        if (n == null) {
            finish();
            return;
        }
        findViewById(R.id.notice_detail_pinned).setVisibility(n.IsPinned ? View.VISIBLE : View.GONE);
        ContentUi.textOrGone((TextView) findViewById(R.id.notice_detail_category), n.Category);
        ((TextView) findViewById(R.id.notice_detail_title)).setText(n.Title);
        TextView date = findViewById(R.id.notice_detail_date);
        date.setText(ContentUi.dateTime(n.PublishAt));
        ContentUi.icon(date, R.drawable.ic_cc_calendar, R.color.cc_slate, 15);

        RecyclerView images = findViewById(R.id.notice_detail_images);
        if (n.Images != null && !n.Images.isEmpty()) {
            images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            images.setAdapter(new PhotoStripAdapter(this, n.Images));
        } else {
            images.setVisibility(View.GONE);
        }
        ContentUi.textOrGone((TextView) findViewById(R.id.notice_detail_content), TextUtils.isEmpty(n.Content) ? null : n.Content.trim());
        int files = ContentUi.files((LinearLayout) findViewById(R.id.notice_detail_files), n.Files);
        findViewById(R.id.notice_detail_files_card).setVisibility(files > 0 ? View.VISIBLE : View.GONE);
    }
}
