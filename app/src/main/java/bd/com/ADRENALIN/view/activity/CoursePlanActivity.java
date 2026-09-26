package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.view.adapter.CoursePlanRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class CoursePlanActivity extends BaseActivity {

    public static final String TAG = CoursePlanActivity.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_plan);
        ButterKnife.bind(this);

        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CoursePlanActivity.this.setTitle(getString(R.string.lbl_course_plan));

        getCoursePlanFromApi();
    }

    private void getCoursePlanFromApi() {
        ExamType examTypeSelected = getPrefManager().getExamTypeSelected();
        if (examTypeSelected == null) {
            showMsg("No Next Exam Info");
            return;
        }

        RetrofitClient.getApiService(context).getCoursePlan(examTypeSelected.getId()).enqueue(new ApiCallback<List<String>>(context,
                new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        List<String> coursePlanList = response.body();
                        setDataToAdapter(coursePlanList);
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                    }
                }));
    }

    private void setDataToAdapter(List<String> bookList) {
        if (bookList != null && !bookList.isEmpty()) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new CoursePlanRecyclerViewAdapter(context, bookList));
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }
    }

}
