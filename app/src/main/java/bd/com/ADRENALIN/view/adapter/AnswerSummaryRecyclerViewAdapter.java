package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.AnswerSummary;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.view.activity.AnswerSummaryActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/6/17.
 */

public class AnswerSummaryRecyclerViewAdapter extends
        RecyclerView.Adapter<AnswerSummaryRecyclerViewAdapter.AnswerSummaryViewHolder> {

    private List<AnswerSummary> answerSummaryArrayList;
    private Context mContext;

    public class AnswerSummaryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvBookTitle)
        TextView title;

        public AnswerSummaryViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getAdapterPosition();

                    Intent intent = new Intent(mContext, AnswerSummaryActivity.class);
                    AnswerSummary answerSummary = answerSummaryArrayList.get(itemPosition);
                    intent.putExtra(AppConstants.AnswerSummaryConstants.ANSWER_SUMMARY_INTENT_CODE, new Gson().toJson(answerSummary));
                    mContext.startActivity(intent);

                    AppUtils.startActivityAnimation(mContext);
                }
            });
        }


    }

    public AnswerSummaryRecyclerViewAdapter(Context context, List<AnswerSummary> items) {
        mContext = context;
        this.answerSummaryArrayList = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AnswerSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnswerSummaryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_answer_summary_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(AnswerSummaryViewHolder holder, int position) {
        AnswerSummary answerSummary = answerSummaryArrayList.get(position);
        holder.title.setText(answerSummary.getName());
    }

    @Override
    public int getItemCount() {
        return answerSummaryArrayList.size();
    }
}
