package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Routine;
import bd.com.ADRENALIN.util.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/6/17.
 */

public class RoutineRecyclerViewAdapter extends
        RecyclerView.Adapter<RoutineRecyclerViewAdapter.RoutineViewHolder> {

    private List<Routine> routineArrayList;
    private Context mContext;

    public class RoutineViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv)
        CardView cardView;
        @BindView(R.id.tvRoutineTitle)
        TextView title;
        @BindView(R.id.tvRoutineExamDateAndStatus)
        TextView dataAndStatus;

        @BindView(R.id.tvRoutineContent)
        ExpandableTextView content;

        public RoutineViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

        }
    }

    public RoutineRecyclerViewAdapter(Context context, List<Routine> items) {
        mContext = context;
        this.routineArrayList = items;
    }

    @Override
    public RoutineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RoutineViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_routine, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final RoutineViewHolder holder, int position) {

        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, position % 2 == 0 ? R.color.white : R.color.white_grayish));

        Routine routine = routineArrayList.get(position);
        holder.title.setText(routine.getName());

        SpannableString statusSpannableString = new SpannableString(" (" + routine.getStatus() + ")");
        statusSpannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, statusSpannableString.length(), 0);

        SpannableString nameStatus = new SpannableString(AppUtils.getDateTimeStringFromDate(AppUtils.getDateFromString(routine.getExamDate())));

        holder.dataAndStatus.setText(TextUtils.concat(nameStatus, " ", statusSpannableString));
        holder.content.setText(routine.getContent());

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.content.toggle();
            }
        });
    }

    @Override
    public int getItemCount() {
        return routineArrayList.size();
    }
}
