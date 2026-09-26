package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.Archive;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.view.activity.ExamActivity;
import bd.com.ADRENALIN.view.activity.ExamDiscussionActivity;
import bd.com.ADRENALIN.view.activity.PaymentActivityForArchive;
import bd.com.ADRENALIN.pojo.Exam;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.ExamUserStatus;
import bd.com.ADRENALIN.pojo.ResponseJson;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.view.activity.ExamAnswerActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/6/17.
 */

public class ArchiveListRecyclerViewAdapter extends
        RecyclerView.Adapter<ArchiveListRecyclerViewAdapter.ArchiveListViewHolder> {

    private List<Archive> archiveList;
    private Context context;
    private User user;
    private boolean fromArchive;

    public class ArchiveListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv)
        CardView cardView;
        @BindView(R.id.tvArchiveName)
        TextView tvArchiveName;
        @BindView(R.id.tvArchiveDate)
        TextView tvArchiveDate;
        @BindView(R.id.tvArchiveContent)
        ExpandableTextView tvArchiveContent;

        public ArchiveListViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int itemPosition = getAdapterPosition();
                    final Archive archive = archiveList.get(itemPosition);
                    int msgIndex=archive.getIsPaid() == 0?R.string.lbl_exam_show_or_ans:R.string.lbl_exam_show_or_discussion;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
                    builder.setMessage(context.getString(msgIndex))
                            .setNegativeButton(context.getString(R.string.btn_question_show), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (archive.getIsPaid() == 0) {
                                        new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                                                .setMessage(context.getString(R.string.lbl_show_question_wo_performing_exam_first))
                                                .setPositiveButton(context.getString(R.string.btn_show_only_question), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        //showQuestionWithAnswerWithPayment(itemPosition);
                                                        showQuestionWithAnswer(itemPosition);
                                                        dialogInterface.dismiss();
                                                    }
                                                })
                                                .setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    } else {
                                        showQuestionWithAnswer(itemPosition);
                                    }
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.setNeutralButton(context.getString(R.string.btn_exam_discussion), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(context, ExamDiscussionActivity.class);
                            intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, archiveList.get(itemPosition).getId());
                            context.startActivity(intent);
                            AppUtils.startActivityAnimation(context);
                            dialogInterface.dismiss();
                        }
                    });

                    if (archive.getIsPaid() == 0) {
                        builder.setPositiveButton(context.getString(R.string.btn_exam_perform), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startExam(itemPosition);
                                dialogInterface.dismiss();
                            }
                        });
                    }

                    builder.setCancelable(true)
                            .show();
                }
            });
        }
    }



    private void startExam(final int itemPosition) {


        final Archive archive = archiveList.get(itemPosition);
        ExamUserStatus status=new ExamUserStatus(){};
status.UserId=user.getId();
        status.ExamId=archive.getId();
status.Status="Opened";
        RetrofitClient.getApiService(context).sendExamUserStatus(status ).enqueue(new ApiCallback<ResponseJson>(context,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson result = response.body();
                        if(!result.IsError){
                            Intent intent = new Intent(context, ExamActivity.class);

                            Exam exam = new Exam();
                            exam.setId(archive.getId());
                            exam.setDuration(archive.getD());
                            exam.setCategoryId(archive.getCategoryId());
                            intent.putExtra(AppConstants.ExamConstants.INTENT_CODE, new Gson().toJson(exam));
                            context.startActivity(intent);
                            AppUtils.startActivityAnimation(context);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        LOG.e("getArchives", " Error "+t.getMessage());

                    }
                }));


    }

    private void showQuestionWithAnswer(int itemPosition) {



        final Archive archive = archiveList.get(itemPosition);
        ExamUserStatus status=new ExamUserStatus(){};
        status.UserId=user.getId();
        status.ExamId=archive.getId();

        RetrofitClient.getApiService(context).sendExamUserStatus(status ).enqueue(new ApiCallback<ResponseJson>(context,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson result = response.body();
                        if(!result.IsError){

                            Intent intent = new Intent(context, ExamAnswerActivity.class);
                            intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, archive.getId());
                            intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_CATEGORY, archive.getCategoryId());
                            intent.putExtra(AppConstants.ExamConstants.INTENT_FROM_ARCHIVE, fromArchive);
                            context.startActivity(intent);
                            AppUtils.startActivityAnimation(context);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {
                        LOG.e("getArchives", " Error "+t.getMessage());

                    }
                }));

    }

    private void showQuestionWithAnswerWithPayment(int itemPosition) {
        Intent intent = new Intent(context, PaymentActivityForArchive.class);
        Archive archive = archiveList.get(itemPosition);
        intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, archive.getId());
        intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_CATEGORY, archive.getCategoryId());
        intent.putExtra(AppConstants.ExamConstants.INTENT_FROM_ARCHIVE, fromArchive);
        context.startActivity(intent);
        AppUtils.startActivityAnimation(context);
    }

    public ArchiveListRecyclerViewAdapter(Context context, List<Archive> items,User user, boolean fromArchive) {
        this.context = context;
        this.archiveList = items;
        this.fromArchive = fromArchive;
        this.user=user;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArchiveListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArchiveListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_archive_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final ArchiveListViewHolder holder, int position) {
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, position % 2 == 0 ? R.color.white : R.color.white_grayish));

        Archive archive = archiveList.get(position);
        holder.tvArchiveName.setText(archive.getName());
        holder.tvArchiveDate.setText(AppUtils.getDateStringFromDate(AppUtils.getDateFromString(archive.getStartAt())));
        holder.tvArchiveContent.setText(archive.getContent());

        holder.tvArchiveContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.tvArchiveContent.toggle();
            }
        });
    }

    @Override
    public int getItemCount() {
        return archiveList.size();
    }
}
