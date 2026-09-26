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
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/6/17.
 */

public class NoticeListRecyclerViewAdapter extends
        RecyclerView.Adapter<NoticeListRecyclerViewAdapter.NoticeListViewHolder> {

    private List<String> noticeList;
    private Context context;

    public class NoticeListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv)
        CardView cardView;
        @BindView(R.id.tvNotice)
        TextView tvNotice;

        public NoticeListViewHolder(View v) {
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

    public NoticeListRecyclerViewAdapter(Context context, List<String> items) {
        this.context = context;
        this.noticeList = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoticeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoticeListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notice_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(NoticeListViewHolder holder, int position) {
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, position % 2 == 0 ? R.color.white : R.color.white_grayish));

        String notice = noticeList.get(position);
        if (!notice.isEmpty())
            holder.tvNotice.setText(notice);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }
}
