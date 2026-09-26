package bd.com.ADRENALIN.view.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import bd.com.ADRENALIN.pojo.ImageModel;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.view.adapter.CustomPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by iqrasys on 9/5/2017.
 */

public class FullScreenImageViewerActivity extends BaseActivity {

    public static final String TAG = AppReviewActivity.class.getSimpleName();
    private BaseActivity context;
    private boolean isLoading=true,isDeletable=false;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount,pageNumber=0;
    private User user;

    @BindView(R.id.btnBack)
    AppCompatButton btnBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_viewer);
        ButterKnife.bind(this);
        context = this;
        user = getPrefManager().getUserInfo();
        Intent intent = getIntent();
        setButtonListeners();
        try {
            String arrStr=intent.getStringExtra(AppConstants.ExamDiscussionConstants.EXAM_DISCUSSION_ALL_IMAGES);
            String url=intent.getStringExtra(AppConstants.ExamDiscussionConstants.EXAM_DISCUSSION_IMAGE_URL);
            isDeletable=intent.getBooleanExtra(AppConstants.ExamDiscussionConstants.EXAM_DISCUSSION_IMAGE_IS_EDITABLE,false);
            Log.e("SamarukWebSocket","EXAM_DISCUSSION_ALL_IMAGES "+arrStr);
            ArrayList<Integer> idList = new Gson().fromJson(arrStr, new TypeToken<ArrayList<Integer>>(){}.getType());
            Log.e("SamarukWebSocket","EXAM_DISCUSSION_ALL_IMAGES size"+idList.size());
            ArrayList<ImageModel> list =new ArrayList<ImageModel>();
            for (int id:idList) {
                list.add(new ImageModel(id,url));
            }
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new CustomPagerAdapter(this,list,isDeletable));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setButtonListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
    }
}
