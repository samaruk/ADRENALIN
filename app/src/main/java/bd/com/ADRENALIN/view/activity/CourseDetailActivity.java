package bd.com.ADRENALIN.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.content.Course;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.ContentUi;
import bd.com.ADRENALIN.view.adapter.content.CourseCardBinder;

/** One course from All Courses: the card, full description, what it includes and its course plan. */
public class CourseDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.lbl_course_details));

        final Course course = new Gson().fromJson(getIntent().getStringExtra(AllCoursesActivity.EXTRA_COURSE), Course.class);
        if (course == null) {
            finish();
            return;
        }
        CourseCardBinder.bind(findViewById(R.id.course_header), course, true);

        TextView description = findViewById(R.id.course_full_description);
        description.setText(course.Description);
        findViewById(R.id.course_about_card).setVisibility(TextUtils.isEmpty(course.Description) ? View.GONE : View.VISIBLE);

        int highlights = ContentUi.bullets((LinearLayout) findViewById(R.id.course_highlights), course.Highlights, R.drawable.ic_cc_check, R.color.cc_teal);
        findViewById(R.id.course_includes_card).setVisibility(highlights > 0 ? View.VISIBLE : View.GONE);

        TextView planButton = findViewById(R.id.course_plan_button);
        if (course.PlanCount > 0) {
            planButton.setText(getString(R.string.lbl_view_course_plan) + " (" + (course.PlanCount == 1
                    ? getString(R.string.lbl_course_plan_one_point) : getString(R.string.lbl_course_plan_points, course.PlanCount)) + ")");
            planButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CourseDetailActivity.this, CoursePlanDetailActivity.class);
                    intent.putExtra(CoursePlanDetailActivity.EXTRA_TYPE_ID, course.TypeId);
                    intent.putExtra(CoursePlanDetailActivity.EXTRA_TITLE, TextUtils.isEmpty(course.Title) ? course.Name : course.Title);
                    startActivity(intent);
                    AppUtils.startActivityAnimation(CourseDetailActivity.this);
                }
            });
        } else {
            planButton.setVisibility(View.GONE);
        }
    }
}
