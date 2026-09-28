package bd.com.ADRENALIN.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.content.ContentResponse;
import bd.com.ADRENALIN.pojo.content.Course;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.view.adapter.content.RowAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Course Plan: the list of courses; a course opens its plan point by point (managed on /ExamType). */
public class CoursePlanActivity extends ContentListActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        onCreate(savedInstanceState, getString(R.string.lbl_course_plan));
    }

    @Override
    protected void load() {
        showLoading();
        RetrofitClient.getApiService(this).getCourses().enqueue(new Callback<ContentResponse<List<Course>>>() {
            @Override
            public void onResponse(Call<ContentResponse<List<Course>>> call, Response<ContentResponse<List<Course>>> response) {
                ContentResponse<List<Course>> body = response.body();
                if (body == null || body.IsError) {
                    showMessage(errorText(body != null ? body.Msg : null), true);
                    return;
                }
                if (body.Data == null || body.Data.isEmpty()) {
                    showMessage(getString(R.string.lbl_no_courses), true);
                    return;
                }
                List<RowAdapter.Row> rows = new ArrayList<>();
                for (Course c : body.Data) {
                    String points = c.PlanCount == 0 ? getString(R.string.lbl_course_plan_empty)
                            : c.PlanCount == 1 ? getString(R.string.lbl_course_plan_one_point) : getString(R.string.lbl_course_plan_points, c.PlanCount);
                    String subtitle = TextUtils.isEmpty(c.BatchLabel) ? points : c.BatchLabel + "  ·  " + points;
                    rows.add(RowAdapter.Row.item(TextUtils.isEmpty(c.Title) ? c.Name : c.Title, subtitle, c.ImageUrl, c));
                }
                showList();
                list.setAdapter(new RowAdapter(rows, new RowAdapter.OnRowClick() {
                    @Override
                    public void onRow(RowAdapter.Row row) {
                        Course course = (Course) row.tag;
                        Intent intent = new Intent(CoursePlanActivity.this, CoursePlanDetailActivity.class);
                        intent.putExtra(CoursePlanDetailActivity.EXTRA_TYPE_ID, course.TypeId);
                        intent.putExtra(CoursePlanDetailActivity.EXTRA_TITLE, row.title);
                        startActivity(intent);
                        AppUtils.startActivityAnimation(CoursePlanActivity.this);
                    }
                }));
            }

            @Override
            public void onFailure(Call<ContentResponse<List<Course>>> call, Throwable t) {
                showMessage(getString(R.string.err_network), true);
            }
        });
    }
}
