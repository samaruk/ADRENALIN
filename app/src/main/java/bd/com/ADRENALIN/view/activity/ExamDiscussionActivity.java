package bd.com.ADRENALIN.view.activity;

/**
 * Created by iqrasys on 8/19/2017.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.ExamDiscussion;
import bd.com.ADRENALIN.pojo.ImageModel;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.ExamDiscussionPostModel;
import bd.com.ADRENALIN.pojo.ResponseJson;
import bd.com.ADRENALIN.pojo.ResponseJsonList;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.service.CommentPopup;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.view.adapter.ExamDiscussionRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class ExamDiscussionActivity extends BaseActivity implements CommentPopup.CommentSaveListener{

    public static final String TAG = AppReviewActivity.class.getSimpleName();
    private ExamDiscussionActivity context;
    private ExamDiscussionRecyclerViewAdapter adpt;
    private boolean isLoading=true;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount,pageNumber=0;
    private User user;
    private int examId;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private File sourceFile;
    public CommentPopup popup;

    @BindView(R.id.layoutView)
    RelativeLayout layoutView;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnReview)
    AppCompatButton btnReview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_discussion);
        ButterKnife.bind(this);

        context = this;
        user = getPrefManager().getUserInfo();
        Intent intent = getIntent();
        if (intent != null) {
            examId=intent.getIntExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, 0);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ExamDiscussionActivity.this.setTitle(getString(R.string.lbl_exam_discussion));

        requestStoragePermission();

        setDataToAdapter();
        setButtonListeners();

    }
    private void setButtonListeners() {
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenCreateComment();
            }
        });
    }
    private void onOpenCreateComment() {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_comment, layoutView, false);
        popup=new CommentPopup(this,user,this,layoutView,view);
    }
    private void setDataToAdapter() {
        adpt=new ExamDiscussionRecyclerViewAdapter(context, new ArrayList<ExamDiscussion>(),user,layoutView,examId);
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
        RetrofitClient.getApiService(context).getExamDiscussion(user.getId(),examId,0,10,pageNumber).enqueue(new ApiCallback<ResponseJsonList<ArrayList<Object>>>(context,false,
                new Callback<ResponseJsonList<ArrayList<Object>>>() {
                    @Override
                    public void onResponse(Call<ResponseJsonList<ArrayList<Object>>> call, Response<ResponseJsonList<ArrayList<Object>>> response) {
                        ResponseJsonList<ArrayList<Object>> list = response.body();
                        ArrayList<ExamDiscussion> reviewList=new ArrayList<ExamDiscussion>();
                        try {
                            for (ArrayList<Object> arr : list.Data) {
                                reviewList.add(ExamDiscussion.get(arr));
                            }
                            isLoading = reviewList.size()!=10;
                            adpt.RemoveLoading();
                            adpt.AddItem(reviewList);
                            Log.e("SamarukWebSocket","ExamDiscussion  "+reviewList.size());
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("SamarukWebSocket","Call onActivityResult Successfull");
        popup.onActivityResult(requestCode,resultCode,data);

    }
    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSave(final ArrayList<Bitmap> images, final String text, String identifier) {
        ExamDiscussionPostModel review = new ExamDiscussionPostModel();
        ExamType type= getPrefManager().getExamTypeSelected();
        review.Content=text;
        review.ExamId=examId;
        review.CreatedBy=user.getId();
        review.Identifier=identifier;
        review.Images=images.size();
        review.ExamTypeId=type.getId();
        review.ExamTypeName=type.getName();

        RetrofitClient.getApiService(context).createDiscussion(review).enqueue(new ApiCallback<ResponseJson>(context,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson result = response.body();
                        //btnReview.setEnabled(true);
                        if (result != null && !result.IsError){
                            //txtReview.setText("");
                            ExamDiscussion item =new ExamDiscussion(result.Id,text
                                    , AppUtils.getSlashSeparatedDateStringFromDate(Calendar.getInstance().getTime()),
                                    user.getName(), ImageModel.GetList(images));
                            adpt.AddItem(item);
                            popup.Dismiss();
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
}
