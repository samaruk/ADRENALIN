package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.pojo.Routine;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.view.adapter.RoutineRecyclerViewAdapter;
import bd.com.ADRENALIN.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class RoutineActivity extends BaseActivity {

    public static final String TAG = RoutineActivity.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvRoutineIntro)
    TextView tvRoutineIntro;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);
        ButterKnife.bind(this);

        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RoutineActivity.this.setTitle(getString(R.string.lbl_routine));

        ExamType examType = getPrefManager().getExamTypeSelected();
        if (examType == null) {
            LOG.e(TAG, "No Exam Type");
            return;
        }
        tvRoutineIntro.setText("Showing Routine for " + examType.getName());
        getRoutinesFromApi(examType.getId());
    }

    private void getRoutinesFromApi(int examType) {

        RetrofitClient.getApiService(context).getRoutineByExamType(examType).enqueue(new ApiCallback<List<Routine>>(context,
                new Callback<List<Routine>>() {
                    @Override
                    public void onResponse(Call<List<Routine>> call, Response<List<Routine>> response) {
                        List<Routine> routineList = response.body();
                        setDataToAdapter(routineList);
                    }

                    @Override
                    public void onFailure(Call<List<Routine>> call, Throwable t) {
                    }
                }));
    }

    private void setDataToAdapter(List<Routine> routineList) {
        if (routineList != null && !routineList.isEmpty()) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new RoutineRecyclerViewAdapter(context, routineList));
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }
    }

}
