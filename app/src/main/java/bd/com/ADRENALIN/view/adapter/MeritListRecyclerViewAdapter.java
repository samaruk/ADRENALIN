package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Merit;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/6/17.
 */

public class MeritListRecyclerViewAdapter extends
        RecyclerView.Adapter<MeritListRecyclerViewAdapter.MeritListViewHolder> {

    private List<Merit> meritList;
    private Context context;

    public class MeritListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv)
        CardView cardView;
        @BindView(R.id.tvMeritPosition)
        TextView tvMeritPosition;
        @BindView(R.id.tvMeritName)
        TextView tvMeritName;
        @BindView(R.id.tvMeritMark)
        TextView tvMeritMark;

        public MeritListViewHolder(View v) {
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

    public MeritListRecyclerViewAdapter(Context context, List<Merit> items) {
        this.context = context;
        this.meritList = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MeritListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MeritListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_merit_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(MeritListViewHolder holder, int position) {
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, position % 2 == 0 ? R.color.white : R.color.white_grayish));

        Merit merit = meritList.get(position);
        holder.tvMeritPosition.setText((position + 1) + "");
        holder.tvMeritName.setText(merit.getName());
        holder.tvMeritMark.setText(merit.getMarks() + "");
    }

    @Override
    public int getItemCount() {
        return meritList.size();
    }
}
