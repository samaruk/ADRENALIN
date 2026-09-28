package bd.com.ADRENALIN.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.util.ContentUi;

/** Photos full screen (About Us gallery, notice images): swipe between them, tap the cross to close. */
public class PhotoViewerActivity extends AppCompatActivity {

    public static final String EXTRA_URLS = "urls";
    public static final String EXTRA_INDEX = "index";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ArrayList<String> urls = getIntent().getStringArrayListExtra(EXTRA_URLS);
        if (urls == null || urls.isEmpty()) {
            finish();
            return;
        }
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        ViewPager pager = new ViewPager(this);
        root.addView(pager, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final TextView counter = new TextView(this);
        counter.setTextColor(Color.WHITE);
        counter.setTextSize(14);
        int pad = ContentUi.dp(this, 16);
        counter.setPadding(pad, pad, pad, pad);
        root.addView(counter, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT));

        ImageView close = new ImageView(this);
        ContentUi.tint(close, R.drawable.ic_cc_close, R.color.white);
        close.setPadding(pad, pad, pad, pad);
        close.setContentDescription(getString(R.string.btn_ok));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int size = ContentUi.dp(this, 56);
        root.addView(close, new FrameLayout.LayoutParams(size, size, Gravity.TOP | Gravity.RIGHT));
        setContentView(root);

        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return urls.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                ImageView image = new ImageView(PhotoViewerActivity.this);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(PhotoViewerActivity.this).load(ContentUi.url(urls.get(position))).fitCenter().into(image);
                container.addView(image, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return image;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                counter.setText(getString(R.string.lbl_photo_counter, position + 1, urls.size()));
            }
        });
        int index = Math.max(0, Math.min(urls.size() - 1, getIntent().getIntExtra(EXTRA_INDEX, 0)));
        pager.setCurrentItem(index);
        counter.setText(getString(R.string.lbl_photo_counter, index + 1, urls.size()));
        counter.setVisibility(urls.size() > 1 ? View.VISIBLE : View.GONE);
    }
}
