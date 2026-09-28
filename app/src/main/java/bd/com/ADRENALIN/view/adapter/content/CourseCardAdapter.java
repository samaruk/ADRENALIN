package bd.com.ADRENALIN.view.adapter.content;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.content.Course;

/** All Courses: one card per course. */
public class CourseCardAdapter extends RecyclerView.Adapter<CourseCardAdapter.Holder> {

    public interface OnCourseClick {
        void onCourse(Course course);
    }

    private final List<Course> courses;
    private final OnCourseClick listener;

    public CourseCardAdapter(List<Course> courses, OnCourseClick listener) {
        this.courses = courses;
        this.listener = listener;
    }

    static class Holder extends RecyclerView.ViewHolder {
        Holder(View view) {
            super(view);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_course, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final Course course = courses.get(position);
        CourseCardBinder.bind(holder.itemView, course, false);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCourse(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
