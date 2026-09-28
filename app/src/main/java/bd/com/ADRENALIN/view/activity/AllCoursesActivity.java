package bd.com.ADRENALIN.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.content.ContentResponse;
import bd.com.ADRENALIN.pojo.content.Course;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.view.adapter.content.CourseCardAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** All Courses: every published course with image, classes, tests, price and discount (managed on /ExamType). */
public class AllCoursesActivity extends ContentListActivity {

    public static final String EXTRA_COURSE = "course_json";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onCreate(savedInstanceState, getString(R.string.lbl_all_courses));
    }

    @Override
    protected void load() {
        showLoading();
        RetrofitClient.getApiService(this).getCourses().enqueue(
                new Callback<ContentResponse<List<Course>>>() {
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
                        showList();
                        list.setAdapter(new CourseCardAdapter(body.Data, new CourseCardAdapter.OnCourseClick() {
                            @Override
                            public void onCourse(Course course) {
                                Intent intent = new Intent(AllCoursesActivity.this, CourseDetailActivity.class);
                                intent.putExtra(EXTRA_COURSE, new Gson().toJson(course));
                                startActivity(intent);
                                AppUtils.startActivityAnimation(AllCoursesActivity.this);
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
