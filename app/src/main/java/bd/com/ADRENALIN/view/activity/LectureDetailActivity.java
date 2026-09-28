package bd.com.ADRENALIN.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.pojo.content.ContentFileItem;
import bd.com.ADRENALIN.pojo.content.LectureDetail;
import bd.com.ADRENALIN.pojo.content.LectureDetailResponse;
import bd.com.ADRENALIN.util.ContentUi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * One lecture: video, PDF notes, presentation, overview, learning objectives, the lecture text, notes and
 * attachments. Premium lectures show a lock message for students without access to the course.
 */
public class LectureDetailActivity extends BaseActivity {

    public static final String EXTRA_LECTURE_ID = "lecture_id";
    public static final String EXTRA_TITLE = "title";

    private View scroll, state;
    private ProgressBar progress;
    private TextView message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        setTitle(TextUtils.isEmpty(title) ? getString(R.string.lbl_lecture) : title);
        scroll = findViewById(R.id.lecture_scroll);
        state = findViewById(R.id.content_state);
        progress = findViewById(R.id.content_progress);
        message = findViewById(R.id.content_message);
        load();
    }

    private void load() {
        scroll.setVisibility(View.GONE);
        state.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        message.setText(R.string.lbl_loading);
        state.setOnClickListener(null);
        User user = getPrefManager().getUserInfo();
        long id = getIntent().getLongExtra(EXTRA_LECTURE_ID, 0);
        RetrofitClient.getApiService(this).getLectureContent(id, user != null ? user.getId() : null).enqueue(new Callback<LectureDetailResponse>() {
            @Override
            public void onResponse(Call<LectureDetailResponse> call, Response<LectureDetailResponse> response) {
                LectureDetailResponse body = response.body();
                if (body == null || body.IsError || body.Lecture == null) {
                    fail(body != null && !TextUtils.isEmpty(body.Msg) ? body.Msg : getString(R.string.err_server));
                    return;
                }
                show(body);
            }

            @Override
            public void onFailure(Call<LectureDetailResponse> call, Throwable t) {
                fail(getString(R.string.err_network));
            }
        });
    }

    private void fail(String text) {
        progress.setVisibility(View.GONE);
        message.setText(text + "\n\n" + getString(R.string.lbl_retry));
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
    }

    private static List<ContentFileItem> ofKind(List<ContentFileItem> files, String kind) {
        List<ContentFileItem> out = new ArrayList<>();
        if (files != null) for (ContentFileItem f : files) if (kind.equals(f.Kind)) out.add(f);
        return out;
    }

    /** A button for a link or an uploaded file (the link wins when both exist); hidden when there is neither. */
    private void mediaButton(TextView button, final String link, List<ContentFileItem> files, int icon, int iconColor) {
        final String target = !TextUtils.isEmpty(link) ? link : files.isEmpty() ? null : files.get(files.size() - 1).Url;
        if (target == null) {
            button.setVisibility(View.GONE);
            return;
        }
        button.setVisibility(View.VISIBLE);
        ContentUi.icon(button, icon, iconColor, 20);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentUi.open(LectureDetailActivity.this, target);
            }
        });
    }

    private void show(LectureDetailResponse data) {
        LectureDetail l = data.Lecture;
        state.setVisibility(View.GONE);
        scroll.setVisibility(View.VISIBLE);
        setTitle(l.Title);

        List<ContentFileItem> thumbs = ofKind(data.Files, "Thumbnail");
        ImageView cover = findViewById(R.id.lecture_cover);
        if (thumbs.isEmpty()) cover.setVisibility(View.GONE);
        else ContentUi.image(this, thumbs.get(thumbs.size() - 1).Url, cover, R.drawable.bg_cc_banner);

        List<String> path = new ArrayList<>();
        if (l.LectureNo > 0) path.add(getString(R.string.lbl_lecture_no, l.LectureNo));
        if (!TextUtils.isEmpty(l.Subject)) path.add(l.Subject);
        if (!TextUtils.isEmpty(l.Course)) path.add(l.Course);
        ContentUi.textOrGone((TextView) findViewById(R.id.lecture_path), TextUtils.join("  ·  ", path));
        ((TextView) findViewById(R.id.lecture_detail_title)).setText(l.Title);
        ContentUi.textOrGone((TextView) findViewById(R.id.lecture_detail_topic), l.Topic);

        TextView access = findViewById(R.id.lecture_detail_access);
        if (l.IsFree) {
            access.setText(R.string.lbl_free);
            access.setBackgroundResource(R.drawable.bg_cc_pill_free);
            access.setTextColor(getResources().getColor(R.color.cc_blue));
        } else {
            access.setText(R.string.lbl_premium);
            access.setBackgroundResource(R.drawable.bg_cc_pill_premium);
            access.setTextColor(getResources().getColor(R.color.cc_purple));
            if (data.Locked) ContentUi.icon(access, R.drawable.ic_cc_lock, R.color.cc_purple, 12);
        }
        List<String> meta = new ArrayList<>();
        if (!TextUtils.isEmpty(l.Duration)) meta.add(l.Duration);
        if (!TextUtils.isEmpty(l.UpdatedAt)) meta.add(ContentUi.date(l.UpdatedAt));
        TextView metaView = findViewById(R.id.lecture_detail_meta);
        metaView.setText(TextUtils.join("  ·  ", meta));
        if (!TextUtils.isEmpty(l.Duration)) ContentUi.icon(metaView, R.drawable.ic_cc_clock, R.color.cc_slate, 14);

        View locked = findViewById(R.id.lecture_locked);
        locked.setVisibility(data.Locked ? View.VISIBLE : View.GONE);
        if (data.Locked) {
            ContentUi.tint((ImageView) findViewById(R.id.lecture_locked_icon), R.drawable.ic_cc_lock, R.color.cc_amber_dark);
            ((TextView) findViewById(R.id.lecture_locked_text)).setText(data.LockedMessage);
        }

        mediaButton((TextView) findViewById(R.id.lecture_video_button), l.VideoUrl, ofKind(data.Files, "Video"), R.drawable.ic_cc_play, R.color.white);
        mediaButton((TextView) findViewById(R.id.lecture_pdf_button), l.PdfUrl, ofKind(data.Files, "Pdf"), R.drawable.ic_cc_pdf, R.color.cc_red);
        mediaButton((TextView) findViewById(R.id.lecture_slides_button), l.SlidesUrl, ofKind(data.Files, "Slides"), R.drawable.ic_cc_slides, R.color.cc_amber_dark);
        boolean hasMedia = findViewById(R.id.lecture_video_button).getVisibility() == View.VISIBLE
                || findViewById(R.id.lecture_pdf_button).getVisibility() == View.VISIBLE
                || findViewById(R.id.lecture_slides_button).getVisibility() == View.VISIBLE;
        findViewById(R.id.lecture_media).setVisibility(hasMedia ? View.VISIBLE : View.GONE);
        // Keep the PDF button full width when there is no presentation, and the other way round
        if (findViewById(R.id.lecture_pdf_button).getVisibility() == View.GONE && findViewById(R.id.lecture_slides_button).getVisibility() == View.GONE) {
            ((View) findViewById(R.id.lecture_pdf_button).getParent()).setVisibility(View.GONE);
        }

        ContentUi.textCard(findViewById(R.id.lecture_overview_card), getString(R.string.lbl_about_lecture), l.ShortDescription);
        int objectives = ContentUi.bullets((LinearLayout) findViewById(R.id.lecture_objectives), l.Objectives, R.drawable.ic_cc_check, R.color.cc_teal);
        findViewById(R.id.lecture_objectives_card).setVisibility(objectives > 0 ? View.VISIBLE : View.GONE);
        ContentUi.textCard(findViewById(R.id.lecture_content_card), getString(R.string.lbl_lecture_content), l.Content);
        ContentUi.textCard(findViewById(R.id.lecture_notes_card), getString(R.string.lbl_important_notes), l.Notes);
        int files = ContentUi.files((LinearLayout) findViewById(R.id.lecture_files), ofKind(data.Files, "Attachment"));
        findViewById(R.id.lecture_files_card).setVisibility(files > 0 ? View.VISIBLE : View.GONE);
    }
}
