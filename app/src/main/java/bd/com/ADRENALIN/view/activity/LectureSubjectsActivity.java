package bd.com.ADRENALIN.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.pojo.content.ContentResponse;
import bd.com.ADRENALIN.pojo.content.LectureSubject;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.view.adapter.content.RowAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Lecture: subjects with published lectures, grouped by course (the student's selected course first);
 * a subject opens its lectures. Managed on /Subject in the admin panel.
 */
public class LectureSubjectsActivity extends ContentListActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        onCreate(savedInstanceState, getString(R.string.lbl_lectures));
    }

    @Override
    protected void load() {
        showLoading();
        RetrofitClient.getApiService(this).getLectureSubjects(userId()).enqueue(new Callback<ContentResponse<List<LectureSubject>>>() {
            @Override
            public void onResponse(Call<ContentResponse<List<LectureSubject>>> call, Response<ContentResponse<List<LectureSubject>>> response) {
                ContentResponse<List<LectureSubject>> body = response.body();
                if (body == null || body.IsError) {
                    showMessage(errorText(body != null ? body.Msg : null), true);
                    return;
                }
                if (body.Data == null || body.Data.isEmpty()) {
                    showMessage(getString(R.string.lbl_no_subjects), true);
                    return;
                }
                showList();
                list.setAdapter(new RowAdapter(rows(body.Data), new RowAdapter.OnRowClick() {
                    @Override
                    public void onRow(RowAdapter.Row row) {
                        LectureSubject subject = (LectureSubject) row.tag;
                        Intent intent = new Intent(LectureSubjectsActivity.this, SubjectLecturesActivity.class);
                        intent.putExtra(SubjectLecturesActivity.EXTRA_SUBJECT_ID, subject.Id);
                        intent.putExtra(SubjectLecturesActivity.EXTRA_SUBJECT_NAME, subject.Name);
                        startActivity(intent);
                        AppUtils.startActivityAnimation(LectureSubjectsActivity.this);
                    }
                }));
            }

            @Override
            public void onFailure(Call<ContentResponse<List<LectureSubject>>> call, Throwable t) {
                showMessage(getString(R.string.err_network), true);
            }
        });
    }

    /** Subjects under a heading per course; the course the student selected comes first. */
    private List<RowAdapter.Row> rows(List<LectureSubject> subjects) {
        ExamType selected = getPrefManager().getExamTypeSelected();
        long selectedId = selected != null ? selected.getId() : -1;
        List<LectureSubject> ordered = new ArrayList<>();
        for (LectureSubject s : subjects) if (s.TypeId == selectedId) ordered.add(s);
        for (LectureSubject s : subjects) if (s.TypeId != selectedId) ordered.add(s);

        List<RowAdapter.Row> rows = new ArrayList<>();
        String lastCourse = null;
        boolean several = false;
        for (LectureSubject s : ordered) {
            if (lastCourse != null && !lastCourse.equals(s.Course)) several = true;
            lastCourse = s.Course;
        }
        lastCourse = null;
        for (LectureSubject s : ordered) {
            String course = s.Course == null ? "" : s.Course;
            if (several && !course.equals(lastCourse) && !TextUtils.isEmpty(course)) rows.add(RowAdapter.Row.section(course));
            lastCourse = course;
            String count = s.LectureCount == 1 ? getString(R.string.lbl_lecture_one) : getString(R.string.lbl_lectures_count, s.LectureCount);
            rows.add(RowAdapter.Row.item(s.Name, several || TextUtils.isEmpty(course) ? count : course + "  ·  " + count, null, s));
        }
        return rows;
    }
}
