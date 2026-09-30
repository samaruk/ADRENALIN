package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.pojo.Routine;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.ExamSearch;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.view.adapter.RoutineRecyclerViewAdapter;
import bd.com.ADRENALIN.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Routine of the selected exam type: latest exam first, with a search field.
 *
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

    private final List<Routine> allRoutines = new ArrayList<>();
    private RoutineRecyclerViewAdapter adapter;
    private ExamSearch search;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);
        ButterKnife.bind(this);

        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RoutineActivity.this.setTitle(getString(R.string.lbl_routine));

        adapter = new RoutineRecyclerViewAdapter(context, new ArrayList<Routine>());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        search = new ExamSearch(this, new ExamSearch.Listener() {
            @Override
            public void onQuery(String query) {
                applyFilter();
            }
        });

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
            allRoutines.clear();
            allRoutines.addAll(routineList);
            // Latest exam first, oldest at the bottom
            Collections.sort(allRoutines, new Comparator<Routine>() {
                @Override
                public int compare(Routine a, Routine b) {
                    Date da = AppUtils.getDateFromString(a.getExamDate());
                    Date db = AppUtils.getDateFromString(b.getExamDate());
                    if (da == null && db == null) return 0;
                    if (da == null) return 1;
                    if (db == null) return -1;
                    return db.compareTo(da);
                }
            });
            applyFilter();
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }
    }

    /** Shows the exams that match the search field (all of them when it is empty). */
    private void applyFilter() {
        String query = search.query();
        List<Routine> shown = new ArrayList<>();
        for (Routine r : allRoutines) {
            Date date = AppUtils.getDateFromString(r.getExamDate());
            String dateText = date == null ? "" : AppUtils.getDateTimeStringFromDate(date);
            if (ExamSearch.matches(query, r.getName(), r.getContent(), r.getStatus(), r.getExamDate(), dateText)) shown.add(r);
        }
        adapter.setItems(shown);
        search.showResult(shown.size(), allRoutines.size());
    }
}
