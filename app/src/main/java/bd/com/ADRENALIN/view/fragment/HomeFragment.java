package bd.com.ADRENALIN.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.view.adapter.HomeRecyclerViewAdapter;
import bd.com.ADRENALIN.view.custom.ItemOffsetDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class HomeFragment extends BaseFragment {

    public static final String TAG = HomeFragment.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.nesterScrollViewNextExamSection)
    NestedScrollView nesterScrollViewNextExamSection;

    @BindView(R.id.tvNextExamLabel)
    TextView tvNextExamLabel;
    @BindView(R.id.tvNextExamStartAt)
    TextView tvNextExamStartAt;
    @BindView(R.id.tvNextExamEndAt)
    TextView tvNextExamEndAt;
    @BindView(R.id.tvNextExamDuration)
    TextView tvNextExamDuration;

    @BindView(R.id.tvNextExamContent)
    ExpandableTextView tvNextExamContent;

    @BindView(R.id.imgFbLike)
    ImageView imgFbLike;

    boolean isOnCreateCalled = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOnCreateCalled = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        showMsg("on resume");
        if (AppConstants.SHOULD_RELOAD_NEXT_EXAM_INFO && !isOnCreateCalled) {
            AppConstants.SHOULD_RELOAD_NEXT_EXAM_INFO = false;
            getNextExamInfo();
        }
        isOnCreateCalled = false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        getNextExamInfo();
        setDataToAdapter();

        imgFbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.fb_page))));
            }
        });
    }


    private void setDataToAdapter() {
        List<String> homeItems = new ArrayList<>();
        homeItems.add(getString(R.string.lbl_enter_exam));
        homeItems.add(getString(R.string.lbl_routine));
        homeItems.add(getString(R.string.lbl_result));
        homeItems.add(getString(R.string.lbl_archive));
        homeItems.add(getString(R.string.lbl_book_list));
        homeItems.add(getString(R.string.lbl_course_plan));
        homeItems.add(getString(R.string.lbl_app_review));
        homeItems.add(getString(R.string.lbl_lecture));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.recycler_view_item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(new HomeRecyclerViewAdapter(context, homeItems));

    }

    private void getNextExamInfo() {
        ExamType examType = getPrefManager().getExamTypeSelected();
        User user = getPrefManager().getUserInfo();

        if (user == null || examType == null) return;

//        /*Get next Exam by type*/
        RetrofitClient.getApiService(context)
                .getNextExamByType(examType.getId(), user.getId()).enqueue(new ApiCallback<List<Exam>>(context,
                new Callback<List<Exam>>() {
                    @Override
                    public void onResponse(Call<List<Exam>> call, Response<List<Exam>> response) {
                        List<Exam> examList = response.body();

                        if (examList != null && !examList.isEmpty()) {
                            setNextExamData(examList.get(0));
                            getPrefManager().setNextExamInfo(examList.get(0));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Exam>> call, Throwable t) {
                    }
                }));
    }

    private void setNextExamData(Exam nextExam) {
        Date startAtDate = AppUtils.getDateFromString(nextExam.getStartAt());
        Date endAtDate = AppUtils.getDateFromString(nextExam.getEndAt());

        String dateStringWithDayOfWeek = AppUtils.getDateStringWithDayOfWeekFromDate(startAtDate);
        String startTime = AppUtils.getTimeStringFromDate(startAtDate);
        String endTime = AppUtils.getTimeStringFromDate(endAtDate);

        Date today = Calendar.getInstance().getTime();
        if (today.compareTo(startAtDate) >= 0 && today.compareTo(endAtDate) <= 0) {
            tvNextExamLabel.setText("Running Exam");
        }

        tvNextExamStartAt.setText(AppUtils.getDateStringWithDayOfWeekFromDate(startAtDate));
        tvNextExamEndAt.setText(AppUtils.getDateStringWithDayOfWeekFromDate(endAtDate));
        tvNextExamDuration.setText(startTime + " to " + endTime);

        if (nextExam.getContent() != null) {
            tvNextExamContent.setText(nextExam.getContent());
            tvNextExamContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvNextExamContent.toggle();
                }
            });
        }


        if (nextExam.isStarted()) {
            tvNextExamLabel.setText(getString(R.string.lbl_resume_exam_hint));
        }
        nesterScrollViewNextExamSection.setVisibility(View.VISIBLE);
    }
}
