package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/6/17.
 */

public class AboutUsListRecyclerViewAdapter extends
        RecyclerView.Adapter<AboutUsListRecyclerViewAdapter.AboutUsListViewHolder> {

    public static final String TAG = AboutUsListRecyclerViewAdapter.class.getSimpleName();
    private List<Integer> idList;
    private Context context;

    public class AboutUsListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgAboutUs)
        ImageView imgAboutUs;

        public AboutUsListViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getAdapterPosition();
                }
            });
        }
    }

    public AboutUsListRecyclerViewAdapter(Context context, List<Integer> items) {
        this.context = context;
        this.idList = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AboutUsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AboutUsListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_about_us, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(AboutUsListViewHolder holder, int position) {
        int imgId = idList.get(position);
        LOG.e(TAG, RetrofitClient.ABOUT_US_IMG_URL + imgId);
        Glide.with(context)
                .load(RetrofitClient.ABOUT_US_IMG_URL + imgId)
                .thumbnail(0.5f).crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgAboutUs);
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }
}
