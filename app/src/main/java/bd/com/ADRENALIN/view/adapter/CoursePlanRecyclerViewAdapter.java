package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
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

public class CoursePlanRecyclerViewAdapter extends
        RecyclerView.Adapter<CoursePlanRecyclerViewAdapter.CoursePlanViewHolder> {

    private List<String> coursePlanList;
    private Context mContext;

    public class CoursePlanViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvCoursePlanContent)
        TextView tvCoursePlanContent;

        public CoursePlanViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }


    }

    public CoursePlanRecyclerViewAdapter(Context context, List<String> items) {
        mContext = context;
        this.coursePlanList = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CoursePlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CoursePlanViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_course_plan, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(CoursePlanViewHolder holder, int position) {
        holder.tvCoursePlanContent.setText(coursePlanList.get(position));
    }

    @Override
    public int getItemCount() {
        return coursePlanList.size();
    }
}
