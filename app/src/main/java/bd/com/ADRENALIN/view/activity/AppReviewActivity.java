package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.AppReview;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.AppReviewPostModel;
import bd.com.ADRENALIN.pojo.ResponseJson;
import bd.com.ADRENALIN.pojo.ResponseJsonList;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.view.adapter.AppReviewRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class AppReviewActivity extends BaseActivity {

    public static final String TAG = AppReviewActivity.class.getSimpleName();
    private Context context;
    private AppReviewActivity that;
    private  AppReviewRecyclerViewAdapter adpt;
    private boolean isLoading=true;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount,pageNumber=0;
    private User user;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtReview)
    EditText txtReview;
    @BindView(R.id.btnReview)
    AppCompatButton btnReview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_review);
        ButterKnife.bind(this);

        context = this;
        that=this;
        user = getPrefManager().getUserInfo();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppReviewActivity.this.setTitle(getString(R.string.lbl_app_review));

        setDataToAdapter();
        setButtonListeners();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                txtReview.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

    }
    private void setButtonListeners() {
        btnReview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createReview();
            }
        });
    }
    private void createReview() {
        final String content=txtReview.getText().toString();
        if (content.isEmpty()) {
            return;
        }

        btnReview.setEnabled(false);

        User user = getPrefManager().getUserInfo();
        AppReviewPostModel review = new AppReviewPostModel();
        review.Content=content;
        review.CreatedBy=user.getId();
        RetrofitClient.getApiService(context).createAppReview(review).enqueue(new ApiCallback<ResponseJson>(context,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson result = response.body();
                        btnReview.setEnabled(true);
                        if (result != null && !result.IsError){
                            txtReview.setText("");
                            AppReview item =new AppReview();
                            item.IsActive=true;
                            item.Content=content;
                            item.CreatedAt=AppUtils.getSlashSeparatedDateStringFromDate(Calendar.getInstance().getTime());
                            item.CreatedBy="Your Review";
                            item.Id=result.Id;
                            adpt.AddItem(item);
                        }
                        else{
                            showMsg(result.Msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {

                    }
                }));

    }


    private void setDataToAdapter() {
        adpt=new AppReviewRecyclerViewAdapter(context, new ArrayList<AppReview>());
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
        RetrofitClient.getApiService(context).getAppReview(user.getId(),15,pageNumber).enqueue(new ApiCallback<ResponseJsonList<ArrayList<Object>>>(context,false,
                new Callback<ResponseJsonList<ArrayList<Object>>>() {
                    @Override
                    public void onResponse(Call<ResponseJsonList<ArrayList<Object>>> call, Response<ResponseJsonList<ArrayList<Object>>> response) {
                        ResponseJsonList<ArrayList<Object>> list = response.body();
                        ArrayList<AppReview> reviewList=new ArrayList<AppReview>();
                        for (ArrayList<Object> arr : list.Data) {
                            reviewList.add(AppReview.get(arr));
                        }
                        isLoading = reviewList.size()!=15;
                        adpt.RemoveLoading();
                        adpt.AddItem(reviewList);
                    }

                    @Override
                    public void onFailure(Call<ResponseJsonList<ArrayList<Object>>> call, Throwable t) {

                    }
                }));
    }
}
