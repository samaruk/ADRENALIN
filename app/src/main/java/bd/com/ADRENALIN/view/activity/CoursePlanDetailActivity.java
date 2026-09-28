package bd.com.ADRENALIN.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.content.Course;
import bd.com.ADRENALIN.pojo.content.CoursePlanResponse;
import bd.com.ADRENALIN.util.ContentUi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** The plan of one course, one numbered point per line. */
public class CoursePlanDetailActivity extends ContentListActivity {

    public static final String EXTRA_TYPE_ID = "type_id";
    public static final String EXTRA_TITLE = "title";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        onCreate(savedInstanceState, TextUtils.isEmpty(title) ? getString(R.string.lbl_course_plan) : title);
    }

    @Override
    protected void load() {
        showLoading();
        long typeId = getIntent().getLongExtra(EXTRA_TYPE_ID, 0);
        RetrofitClient.getApiService(this).getCoursePlanOf(typeId).enqueue(new Callback<CoursePlanResponse>() {
            @Override
            public void onResponse(Call<CoursePlanResponse> call, Response<CoursePlanResponse> response) {
                CoursePlanResponse body = response.body();
                if (body == null || body.IsError || body.Course == null) {
                    showMessage(errorText(body != null ? body.Msg : null), true);
                    return;
                }
                showList();
                list.setAdapter(new PlanAdapter(body));
            }

            @Override
            public void onFailure(Call<CoursePlanResponse> call, Throwable t) {
                showMessage(getString(R.string.err_network), true);
            }
        });
    }

    /** A header card with the course, then the numbered points (or a note that there is no plan yet). */
    private class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final Course course;
        private final List<String> points;
        private final String updatedAt;

        PlanAdapter(CoursePlanResponse plan) {
            course = plan.Course;
            points = plan.Points;
            updatedAt = plan.UpdatedAt;
        }

        private int pointCount() {
            return points == null ? 0 : points.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = viewType == 0 ? R.layout.item_plan_header : R.layout.item_plan_point;
            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            if (viewType == 1) view.setBackgroundColor(getResources().getColor(R.color.cc_card));
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            View view = holder.itemView;
            if (position == 0) {
                ContentUi.image(CoursePlanDetailActivity.this, course.ImageUrl, (ImageView) view.findViewById(R.id.plan_image), R.drawable.bg_cc_banner);
                ((TextView) view.findViewById(R.id.plan_title)).setText(TextUtils.isEmpty(course.Title) ? course.Name : course.Title);
                String subtitle = pointCount() == 0 ? getString(R.string.lbl_no_plan)
                        : (pointCount() == 1 ? getString(R.string.lbl_course_plan_one_point) : getString(R.string.lbl_course_plan_points, pointCount()))
                        + (TextUtils.isEmpty(updatedAt) ? "" : "  ·  " + getString(R.string.lbl_plan_updated, ContentUi.date(updatedAt)));
                ((TextView) view.findViewById(R.id.plan_subtitle)).setText(subtitle);
                return;
            }
            int index = position - 1;
            ((TextView) view.findViewById(R.id.point_number)).setText(String.valueOf(index + 1));
            ((TextView) view.findViewById(R.id.point_text)).setText(points.get(index));
            int pad = ContentUi.dp(CoursePlanDetailActivity.this, 14);
            view.setPadding(pad, index == 0 ? pad : pad / 2, pad, index == pointCount() - 1 ? pad : pad / 2);
        }

        @Override
        public int getItemCount() {
            return 1 + pointCount();
        }
    }
}
