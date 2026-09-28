package bd.com.ADRENALIN.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.content.AboutInfo;
import bd.com.ADRENALIN.pojo.content.AboutResponse;
import bd.com.ADRENALIN.util.ContentUi;
import bd.com.ADRENALIN.view.adapter.content.PhotoStripAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** About Us, as saved on the /AboutUs admin page: texts, contact, links, logo, cover and the photo gallery. */
public class AboutUsActivity extends BaseActivity {

    private View scroll, state;
    private ProgressBar progress;
    private TextView message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.lbl_about_us));
        scroll = findViewById(R.id.about_scroll);
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
        RetrofitClient.getApiService(this).getAboutInfo().enqueue(new Callback<AboutResponse>() {
            @Override
            public void onResponse(Call<AboutResponse> call, Response<AboutResponse> response) {
                AboutResponse body = response.body();
                if (body == null || body.IsError || body.About == null) {
                    fail(body != null && !TextUtils.isEmpty(body.Msg) ? body.Msg : getString(R.string.err_server));
                    return;
                }
                show(body);
            }

            @Override
            public void onFailure(Call<AboutResponse> call, Throwable t) {
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

    private void show(AboutResponse data) {
        final AboutInfo a = data.About;
        state.setVisibility(View.GONE);
        scroll.setVisibility(View.VISIBLE);

        ContentUi.image(this, a.CoverUrl, (ImageView) findViewById(R.id.about_cover), R.drawable.bg_cc_banner);
        ImageView logo = findViewById(R.id.about_logo);
        if (!TextUtils.isEmpty(a.LogoUrl)) Glide.with(this).load(ContentUi.url(a.LogoUrl)).fitCenter().into(logo);

        ((TextView) findViewById(R.id.about_name)).setText(TextUtils.isEmpty(a.Name) ? getString(R.string.app_name) : a.Name);
        ContentUi.textOrGone((TextView) findViewById(R.id.about_tagline), a.Tagline);
        ContentUi.textCard(findViewById(R.id.about_description_card), getString(R.string.lbl_about_us), a.Description);
        ContentUi.textCard(findViewById(R.id.about_mission_card), getString(R.string.lbl_about_mission), a.Mission);
        ContentUi.textCard(findViewById(R.id.about_vision_card), getString(R.string.lbl_about_vision), a.Vision);
        int why = ContentUi.bullets((LinearLayout) findViewById(R.id.about_why), a.Highlights, R.drawable.ic_cc_check, R.color.cc_teal);
        findViewById(R.id.about_why_card).setVisibility(why > 0 ? View.VISIBLE : View.GONE);

        LinearLayout contacts = findViewById(R.id.about_contacts);
        contacts.removeAllViews();
        addContact(contacts, R.drawable.ic_cc_phone, getString(R.string.lbl_contact_phone), a.Phone, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentUi.dial(AboutUsActivity.this, a.Phone);
            }
        });
        addContact(contacts, R.drawable.ic_cc_chat, getString(R.string.lbl_whatsapp), a.WhatsApp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentUi.whatsApp(AboutUsActivity.this, a.WhatsApp);
            }
        });
        addContact(contacts, R.drawable.ic_cc_email, getString(R.string.lbl_contact_email), a.Email, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentUi.email(AboutUsActivity.this, a.Email);
            }
        });
        addContact(contacts, R.drawable.ic_cc_place, getString(R.string.lbl_contact_address), a.Address, TextUtils.isEmpty(a.MapUrl) ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentUi.open(AboutUsActivity.this, a.MapUrl);
            }
        });
        addContact(contacts, R.drawable.ic_cc_clock, getString(R.string.lbl_contact_hours), a.OfficeHours, null);
        findViewById(R.id.about_contact_card).setVisibility(contacts.getChildCount() > 0 ? View.VISIBLE : View.GONE);

        LinearLayout links = findViewById(R.id.about_links);
        links.removeAllViews();
        linkCount = 0;
        addLink(links, R.drawable.ic_cc_web, getString(R.string.lbl_website), a.Website);
        addLink(links, R.drawable.ic_cc_chat, getString(R.string.lbl_facebook), a.FacebookUrl);
        addLink(links, R.drawable.ic_cc_play, getString(R.string.lbl_youtube), a.YouTubeUrl);
        addLink(links, R.drawable.ic_cc_place, getString(R.string.lbl_map), a.MapUrl);
        findViewById(R.id.about_links_card).setVisibility(links.getChildCount() > 0 ? View.VISIBLE : View.GONE);

        RecyclerView gallery = findViewById(R.id.about_gallery);
        if (data.Gallery != null && !data.Gallery.isEmpty()) {
            gallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            gallery.setAdapter(new PhotoStripAdapter(this, data.Gallery));
            findViewById(R.id.about_gallery_card).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.about_gallery_card).setVisibility(View.GONE);
        }
    }

    private void addContact(LinearLayout container, int icon, String label, String value, View.OnClickListener action) {
        if (TextUtils.isEmpty(value)) return;
        View row = LayoutInflater.from(this).inflate(R.layout.item_cc_contact, container, false);
        ContentUi.tint((ImageView) row.findViewById(R.id.contact_icon), icon, R.color.cc_teal);
        ((TextView) row.findViewById(R.id.contact_label)).setText(label);
        ((TextView) row.findViewById(R.id.contact_value)).setText(value);
        if (action != null) row.setOnClickListener(action);
        else row.setBackgroundDrawable(null);
        container.addView(row);
    }

    private int linkCount;

    /** Link buttons, two per row so the labels stay on one line. */
    private void addLink(LinearLayout container, int icon, String label, final String url) {
        if (TextUtils.isEmpty(url)) return;
        LinearLayout row;
        if (linkCount % 2 == 0) {
            row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.topMargin = ContentUi.dp(this, 6);
            container.addView(row, rowParams);
        } else {
            row = (LinearLayout) container.getChildAt(container.getChildCount() - 1);
        }
        linkCount++;
        TextView button = new TextView(this);
        button.setText(label);
        button.setMaxLines(1);
        button.setEllipsize(TextUtils.TruncateAt.END);
        button.setTextSize(13);
        button.setTextColor(getResources().getColor(R.color.cc_ink));
        button.setGravity(android.view.Gravity.CENTER);
        button.setBackgroundResource(R.drawable.bg_cc_button_outline);
        int pad = ContentUi.dp(this, 8);
        button.setPadding(pad, pad + 2, pad, pad + 2);
        button.setCompoundDrawablePadding(ContentUi.dp(this, 4));
        ContentUi.icon(button, icon, R.color.cc_teal, 16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        if (row.getChildCount() > 0) params.leftMargin = ContentUi.dp(this, 8);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentUi.open(AboutUsActivity.this, url);
            }
        });
        row.addView(button, params);
    }
}
