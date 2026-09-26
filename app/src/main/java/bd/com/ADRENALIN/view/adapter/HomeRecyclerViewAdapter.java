package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.ResponseJson;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.view.activity.AnswerSummaryListActivity;
import bd.com.ADRENALIN.view.activity.AppReviewActivity;
import bd.com.ADRENALIN.view.activity.ArchiveActivity;
import bd.com.ADRENALIN.view.activity.BaseActivity;
import bd.com.ADRENALIN.view.activity.BookListActivity;
import bd.com.ADRENALIN.view.activity.CoursePlanActivity;
import bd.com.ADRENALIN.view.activity.ExamActivity;
import bd.com.ADRENALIN.view.activity.LectureActivity;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.ExamUserStatus;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.view.activity.PaymentActivity;
import bd.com.ADRENALIN.view.activity.RoutineActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/6/17.
 */

public class HomeRecyclerViewAdapter extends
        RecyclerView.Adapter<HomeRecyclerViewAdapter.HomeViewHolder> {

    private List<String> homeArrayList;
    private Context context;

    public class HomeViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.btnTitle)
        AppCompatButton btnTitle;


        public HomeViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            btnTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getAdapterPosition();
                    String section = homeArrayList.get(itemPosition);
                    Intent intent = null;

                    if (section.equals(context.getString(R.string.lbl_enter_exam))) {
//                        intent = getPaymentIntent();
                        BaseActivity baseActvt=((BaseActivity) context);
                        final Exam nexExamInfo = baseActvt.getPrefManager().getNexExamInfo();
                        if (nexExamInfo != null) {
                            Date startAtDate = AppUtils.getDateFromString(nexExamInfo.getStartAt());
                            Date endAtDate = AppUtils.getDateFromString(nexExamInfo.getEndAt());
                            Date today = Calendar.getInstance().getTime();


                            if (today.compareTo(startAtDate) >= 0 && today.compareTo(endAtDate) <= 0) {

                                ExamUserStatus status=new ExamUserStatus(){};
                                User user = baseActvt.getPrefManager().getUserInfo();
                                status.UserId=user.getId();
                                status.ExamId=nexExamInfo.getId();
                                status.Status="Opened";
                                RetrofitClient.getApiService(context).sendExamUserStatus(status ).enqueue(new ApiCallback<ResponseJson>(context,
                                        new Callback<ResponseJson>() {
                                            @Override
                                            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                                                ResponseJson result = response.body();
                                                if(!result.IsError){
                                                    Intent intent = new Intent(context, ExamActivity.class);
                                                    intent.putExtra(AppConstants.ExamConstants.INTENT_CODE, new Gson().toJson(nexExamInfo));
                                                    context.startActivity(intent);
                                                    AppUtils.startActivityAnimation(context);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseJson> call, Throwable t) {
                                                LOG.e("getArchives", " Error "+t.getMessage());
                                            }
                                        }));


                            } else {
                                ((BaseActivity) context).showMsg(context.getString(R.string.lbl_exam_now_warning)
                                        + AppUtils.getDateTimeStringFromDate(startAtDate));
                            }
                        }

                    } else if (section.equals(context.getString(R.string.lbl_routine))) {
                        intent = new Intent(context, RoutineActivity.class);
                    } else if (section.equals(context.getString(R.string.lbl_result))) {
                        intent = new Intent(context, AnswerSummaryListActivity.class);
                    } else if (section.equals(context.getString(R.string.lbl_archive))) {
                        intent = new Intent(context, ArchiveActivity.class);
                    } else if (section.equals(context.getString(R.string.lbl_book_list))) {
                        intent = new Intent(context, BookListActivity.class);
                    } else if (section.equals(context.getString(R.string.lbl_course_plan))) {
                        intent = new Intent(context, CoursePlanActivity.class);
                    } else if (section.equals(context.getString(R.string.lbl_app_review))) {
                        intent = new Intent(context, AppReviewActivity.class);
                    } else if (section.equals(context.getString(R.string.lbl_lecture))) {
                        intent = new Intent(context, LectureActivity.class);
                    }

                    if (intent != null) {
                        context.startActivity(intent);
                        AppUtils.startActivityAnimation(context);
                    }

//                    ELearningDetailsFragment eLearningDetailsFragment = new ELearningDetailsFragment();
//                    eLearningDetailsFragment.setArguments(bundle);
//
//                    FragmentTransaction transaction = fragmentManager.beginTransaction();
//                    transaction.addToBackStack(null);
//                    transaction.replace(MainActivity.MAIN_CONTENT_ID, eLearningDetailsFragment).commit();
                }
            });
        }


    }

    private Intent getPaymentIntent(){
        Intent intent = new Intent(context, PaymentActivity.class);
        Exam examNext = ((BaseActivity) context).getPrefManager().getNexExamInfo();
        if (examNext != null)
            intent.putExtra(AppConstants.ExamConstants.INTENT_CODE, new Gson().toJson(examNext));
        return intent;
    }


    private Intent getExamIntent(Exam examNext){
        Intent intent = new Intent(context, ExamActivity.class);
        //Exam examNext = ((BaseActivity) context).getPrefManager().getNexExamInfo();
        //if (examNext != null)
            intent.putExtra(AppConstants.ExamConstants.INTENT_CODE, new Gson().toJson(examNext));
        return intent;
    }

    public HomeRecyclerViewAdapter(Context context, List<String> items) {
        this.context = context;
        this.homeArrayList = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_home, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.btnTitle.setText(homeArrayList.get(position));
//        String String = homeArrayList.get(position);
//        holder.tvCoursePlanContent.setText(String.getTitle());
    }

    @Override
    public int getItemCount() {
        return homeArrayList.size();
    }
}
