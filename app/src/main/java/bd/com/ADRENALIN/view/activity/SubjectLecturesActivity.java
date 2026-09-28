package bd.com.ADRENALIN.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.content.LectureItem;
import bd.com.ADRENALIN.pojo.content.LectureListResponse;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.ContentUi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** The published lectures of one subject, in the order set in the admin panel. */
public class SubjectLecturesActivity extends ContentListActivity {

    public static final String EXTRA_SUBJECT_ID = "subject_id";
    public static final String EXTRA_SUBJECT_NAME = "subject_name";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        String name = getIntent().getStringExtra(EXTRA_SUBJECT_NAME);
        onCreate(savedInstanceState, TextUtils.isEmpty(name) ? getString(R.string.lbl_lectures) : name);
    }

    @Override
    protected void load() {
        showLoading();
        long subjectId = getIntent().getLongExtra(EXTRA_SUBJECT_ID, 0);
        RetrofitClient.getApiService(this).getSubjectLectures(subjectId, userId()).enqueue(new Callback<LectureListResponse>() {
            @Override
            public void onResponse(Call<LectureListResponse> call, Response<LectureListResponse> response) {
                LectureListResponse body = response.body();
                if (body == null || body.IsError) {
                    showMessage(errorText(body != null ? body.Msg : null), true);
                    return;
                }
                if (body.Subject != null && getSupportActionBar() != null && !TextUtils.isEmpty(body.Subject.Course)) {
                    getSupportActionBar().setSubtitle(body.Subject.Course);
                }
                if (body.Data == null || body.Data.isEmpty()) {
                    showMessage(getString(R.string.lbl_no_lectures), true);
                    return;
                }
                showList();
                list.setAdapter(new LectureAdapter(body.Data));
            }

            @Override
            public void onFailure(Call<LectureListResponse> call, Throwable t) {
                showMessage(getString(R.string.err_network), true);
            }
        });
    }

    private class LectureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<LectureItem> lectures;

        LectureAdapter(List<LectureItem> lectures) {
            this.lectures = lectures;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final LectureItem l = lectures.get(position);
            View view = holder.itemView;
            ImageView thumb = view.findViewById(R.id.lecture_thumb);
            ImageView icon = view.findViewById(R.id.lecture_thumb_icon);
            ContentUi.image(SubjectLecturesActivity.this, l.ThumbnailUrl, thumb, R.drawable.bg_cc_banner);
            ContentUi.tint(icon, l.Locked ? R.drawable.ic_cc_lock : l.HasVideo ? R.drawable.ic_cc_play : R.drawable.ic_cc_book, R.color.white);
            icon.setVisibility(TextUtils.isEmpty(l.ThumbnailUrl) || l.Locked ? View.VISIBLE : View.GONE);

            ContentUi.textOrGone((TextView) view.findViewById(R.id.lecture_number), l.LectureNo > 0 ? getString(R.string.lbl_lecture_no, l.LectureNo) : null);
            TextView access = view.findViewById(R.id.lecture_access);
            if (l.IsFree) {
                access.setText(R.string.lbl_free);
                access.setBackgroundResource(R.drawable.bg_cc_pill_free);
                access.setTextColor(getResources().getColor(R.color.cc_blue));
                access.setCompoundDrawables(null, null, null, null);
            } else {
                access.setText(R.string.lbl_premium);
                access.setBackgroundResource(R.drawable.bg_cc_pill_premium);
                access.setTextColor(getResources().getColor(R.color.cc_purple));
                if (l.Locked) ContentUi.icon(access, R.drawable.ic_cc_lock, R.color.cc_purple, 12);
                else access.setCompoundDrawables(null, null, null, null);
            }
            ((TextView) view.findViewById(R.id.lecture_title)).setText(l.Title);
            ContentUi.textOrGone((TextView) view.findViewById(R.id.lecture_topic), TextUtils.isEmpty(l.Topic) ? l.ShortDescription : l.Topic);

            List<String> meta = new ArrayList<>();
            if (!TextUtils.isEmpty(l.Duration)) meta.add(l.Duration);
            if (l.HasVideo) meta.add(getString(R.string.lbl_video));
            if (l.HasPdf) meta.add("PDF");
            if (l.HasSlides) meta.add(getString(R.string.lbl_presentation));
            if (l.AttachmentCount > 0) meta.add(l.AttachmentCount + (l.AttachmentCount == 1 ? " file" : " files"));
            ContentUi.textOrGone((TextView) view.findViewById(R.id.lecture_meta), TextUtils.join("  ·  ", meta));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SubjectLecturesActivity.this, LectureDetailActivity.class);
                    intent.putExtra(LectureDetailActivity.EXTRA_LECTURE_ID, l.Id);
                    intent.putExtra(LectureDetailActivity.EXTRA_TITLE, l.Title);
                    startActivity(intent);
                    AppUtils.startActivityAnimation(SubjectLecturesActivity.this);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lectures.size();
        }
    }
}
