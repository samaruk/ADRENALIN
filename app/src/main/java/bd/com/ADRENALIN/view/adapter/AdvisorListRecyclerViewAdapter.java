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

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/6/17.
 */

public class AdvisorListRecyclerViewAdapter extends
        RecyclerView.Adapter<AdvisorListRecyclerViewAdapter.AdvisorListViewHolder> {

    private List<Integer> idList;
    private Context context;

    public class AdvisorListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgAdvisor)
        ImageView imgAdvisor;

        public AdvisorListViewHolder(View v) {
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

    public AdvisorListRecyclerViewAdapter(Context context, List<Integer> items) {
        this.context = context;
        this.idList = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdvisorListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AdvisorListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_advisor, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(AdvisorListViewHolder holder, int position) {
        int imgId = idList.get(position);

        Glide.with(context)
                .load(RetrofitClient.ADVISOR_IMG_URL + imgId)
                .thumbnail(0.5f).crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgAdvisor);
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }
}
