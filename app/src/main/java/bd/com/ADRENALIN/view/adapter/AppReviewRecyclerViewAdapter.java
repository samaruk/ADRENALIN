package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.AppReview;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/6/17.
 */

public class AppReviewRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppReview> bookArrayList;
    private Context context;

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public class AppReviewViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.cv)
        CardView cardView;
        @BindView(R.id.tvAppReviewUserName)
        TextView title;
        @BindView(R.id.tvAppReviewContent)
        TextView tvAppReviewContent;
        @BindView(R.id.tvAppReviewDate)
        TextView tvAppReviewDate;


        public AppReviewViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public AppReviewRecyclerViewAdapter(Context context, List<AppReview> items) {
        this.context = context;
        this.bookArrayList = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            return new AppReviewViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_app_review, parent, false)
            );
        } else if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, position % 2 == 0 ? R.color.white : R.color.white_grayish));

        if (holder instanceof AppReviewViewHolder) {
            AppReview book = bookArrayList.get(position);
            AppReviewViewHolder itemHolder=(AppReviewViewHolder)holder;
            itemHolder.title.setText(book.CreatedBy);
            itemHolder.tvAppReviewContent.setText(book.Content);
            itemHolder.tvAppReviewDate.setText(book.CreatedAt);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return bookArrayList.get(position) == null ? 1 : 0;
    }

    public void AddItem(AppReview item) {
        bookArrayList.add(0,item);
        notifyDataSetChanged();
    }
    public void AddItem(ArrayList<AppReview> list) {
        bookArrayList.addAll(list);
        notifyDataSetChanged();
    }
    public void SetLoading() {
        bookArrayList.add(null);
        notifyItemInserted(bookArrayList.size() - 1);
    }
    public void RemoveLoading() {
        bookArrayList.remove(bookArrayList.size() - 1);
        notifyItemRemoved(bookArrayList.size());
    }
}
