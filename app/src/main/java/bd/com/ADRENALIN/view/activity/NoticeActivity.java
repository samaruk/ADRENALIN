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
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.content.NoticeFeedResponse;
import bd.com.ADRENALIN.pojo.content.NoticeItem;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.ContentUi;
import bd.com.ADRENALIN.util.NoticeState;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Notices, latest first (pinned ones on top); the ones the student had not seen are marked NEW. */
public class NoticeActivity extends ContentListActivity {

    public static final String EXTRA_NOTICE = "notice_json";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        onCreate(savedInstanceState, getString(R.string.lbl_notices));
        NotificationManagerCompat.from(this).cancelAll();
    }

    @Override
    protected void load() {
        showLoading();
        RetrofitClient.getApiService(this).getNoticeFeed(100).enqueue(new Callback<NoticeFeedResponse>() {
            @Override
            public void onResponse(Call<NoticeFeedResponse> call, Response<NoticeFeedResponse> response) {
                NoticeFeedResponse body = response.body();
                if (body == null || body.IsError) {
                    showMessage(errorText(body != null ? body.Msg : null), true);
                    return;
                }
                if (body.Data == null || body.Data.isEmpty()) {
                    showMessage(getString(R.string.lbl_no_notices), true);
                    return;
                }
                List<Long> ids = new ArrayList<>();
                for (NoticeItem n : body.Data) ids.add(n.Id);
                Set<Long> unseen = NoticeState.markSeen(NoticeActivity.this, ids);
                showList();
                list.setAdapter(new NoticeAdapter(body.Data, unseen));
            }

            @Override
            public void onFailure(Call<NoticeFeedResponse> call, Throwable t) {
                showMessage(getString(R.string.err_network), true);
            }
        });
    }

    private class NoticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<NoticeItem> notices;
        private final Set<Long> unseen;

        NoticeAdapter(List<NoticeItem> notices, Set<Long> unseen) {
            this.notices = notices;
            this.unseen = unseen != null ? unseen : new HashSet<Long>();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final NoticeItem n = notices.get(position);
            View view = holder.itemView;
            view.findViewById(R.id.notice_new).setVisibility(unseen.contains(n.Id) ? View.VISIBLE : View.GONE);
            view.findViewById(R.id.notice_pinned).setVisibility(n.IsPinned ? View.VISIBLE : View.GONE);
            view.findViewById(R.id.notice_accent).setVisibility(n.IsPinned ? View.VISIBLE : View.GONE);
            ContentUi.textOrGone((TextView) view.findViewById(R.id.notice_category), n.Category);
            ((TextView) view.findViewById(R.id.notice_time)).setText(ContentUi.ago(n.PublishAt));
            ((TextView) view.findViewById(R.id.notice_title)).setText(n.Title);
            ContentUi.textOrGone((TextView) view.findViewById(R.id.notice_preview), n.Content == null ? null : n.Content.trim());
            int files = n.Files == null ? 0 : n.Files.size();
            TextView filesView = view.findViewById(R.id.notice_files);
            ContentUi.textOrGone(filesView, files == 0 ? null : files + (files == 1 ? " attachment" : " attachments"));
            if (files > 0) ContentUi.icon(filesView, R.drawable.ic_cc_attach, R.color.cc_slate, 14);
            ImageView image = view.findViewById(R.id.notice_image);
            if (TextUtils.isEmpty(n.ImageUrl)) {
                image.setVisibility(View.GONE);
            } else {
                image.setVisibility(View.VISIBLE);
                ContentUi.image(NoticeActivity.this, n.ImageUrl, image, R.drawable.bg_cc_thumb);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NoticeActivity.this, NoticeDetailActivity.class);
                    intent.putExtra(EXTRA_NOTICE, new Gson().toJson(n));
                    startActivity(intent);
                    AppUtils.startActivityAnimation(NoticeActivity.this);
                }
            });
        }

        @Override
        public int getItemCount() {
            return notices.size();
        }
    }
}
