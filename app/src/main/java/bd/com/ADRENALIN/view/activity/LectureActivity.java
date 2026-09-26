package bd.com.ADRENALIN.view.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.pojo.Routine;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Lecture;
import bd.com.ADRENALIN.pojo.ResponseJsonList;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.view.adapter.LectureRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iqrasys on 9/8/2017.
 */


public class LectureActivity extends BaseActivity {

    public static final String TAG = RoutineActivity.class.getSimpleName();
    private LectureActivity context;
    private User user;
    private ExamType examType;

    private boolean isLoading=true;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount,pageNumber=0;
    private LectureRecyclerViewAdapter adpt;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvLectureIntro)
    TextView tvLectureIntro;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);
        ButterKnife.bind(this);

        context = this;
        user = getPrefManager().getUserInfo();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LectureActivity.this.setTitle(getString(R.string.lbl_lecture));

        examType = getPrefManager().getExamTypeSelected();
        if (examType == null) {
            LOG.e(TAG, "No Exam Type");
            return;
        }
        tvLectureIntro.setText("Showing Lecture for " + examType.getName());
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
        adpt=new LectureRecyclerViewAdapter(context, new ArrayList<Lecture>());
        //recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    onLoadMore();
                    isLoading = true;
                }
            }
        });

        recyclerView.setAdapter(adpt);
        onLoadMore();
    }

    public void onLoadMore() {
        pageNumber++;
        adpt.SetLoading();
        RetrofitClient.getApiService(context).getLecture(user.getId(),examType.getId(),10,pageNumber).enqueue(new ApiCallback<ResponseJsonList<ArrayList<Object>>>(context,false,
                new Callback<ResponseJsonList<ArrayList<Object>>>() {
                    @Override
                    public void onResponse(Call<ResponseJsonList<ArrayList<Object>>> call, Response<ResponseJsonList<ArrayList<Object>>> response) {
                        ResponseJsonList<ArrayList<Object>> list = response.body();
                        ArrayList<Lecture> reviewList=new ArrayList<Lecture>();
                        try {
                            for (ArrayList<Object> arr : list.Data) {
                                reviewList.add(Lecture.get(arr));
                            }
                            isLoading = reviewList.size()!=10;
                            adpt.RemoveLoading();
                            adpt.AddItem(reviewList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("SamarukWebSocket",e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJsonList<ArrayList<Object>>> call, Throwable t) {

                    }
                }));
    }

}

