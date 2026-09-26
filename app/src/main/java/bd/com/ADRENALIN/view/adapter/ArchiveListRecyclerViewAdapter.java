package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
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
 * Archived (result published) exams. Tapping one offers: Reexam, Show Question, Show Discussion.
 *
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
                    showOptions(getAdapterPosition());
                }
            });
        }
    }

    /** "Choose your option." dialog with Reexam / Show Question / Show Discussion. */
    private void showOptions(final int itemPosition) {
        if (itemPosition < 0 || itemPosition >= archiveList.size()) return;
        final Archive archive = archiveList.get(itemPosition);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_archive_options, null);
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                .setView(view)
                .setCancelable(true)
                .create();

        TextView tvInfo = view.findViewById(R.id.tvArchiveInfo);
        if (archive.getReExamCount() > 0) {
            tvInfo.setText(context.getString(R.string.lbl_reexam_count, archive.getReExamCount()));
            tvInfo.setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.btnReexam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                confirmReExam(itemPosition);
            }
        });
        view.findViewById(R.id.btnShowQuestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showQuestionWithAnswer(itemPosition);
            }
        });
        view.findViewById(R.id.btnShowDiscussion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, ExamDiscussionActivity.class);
                intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, archive.getId());
                context.startActivity(intent);
                AppUtils.startActivityAnimation(context);
            }
        });
        dialog.show();
    }

    private void confirmReExam(final int itemPosition) {
        final Archive archive = archiveList.get(itemPosition);
        boolean resuming = user != null && ((bd.com.ADRENALIN.view.activity.BaseActivity) context).getPrefManager()
                .getExamDraft(user.getId(), archive.getId(), true) != null;
        String message = resuming
                ? context.getString(R.string.lbl_reexam_resume)
                : context.getString(R.string.lbl_reexam_confirmation, archive.getD());
        new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                .setMessage(message)
                .setPositiveButton(context.getString(resuming ? R.string.btn_continue : R.string.btn_start), new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startReExam(itemPosition);
                    }
                })
                .setNegativeButton(context.getString(R.string.btn_cancel), new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    /** Starts (or resumes) a re-exam attempt: same questions and duration, never counted in the merit list. */
    private void startReExam(final int itemPosition) {
        final Archive archive = archiveList.get(itemPosition);
        Intent intent = new Intent(context, ExamActivity.class);
        Exam exam = new Exam();
        exam.setId(archive.getId());
        exam.setName(archive.getName());
        exam.setDuration(archive.getD());
        exam.setCategoryId(archive.getCategoryId());
        exam.setTotalQuestion(archive.getTotalQuestion());
        exam.setReExam(true);
        intent.putExtra(AppConstants.ExamConstants.INTENT_CODE, new Gson().toJson(exam));
        intent.putExtra(AppConstants.ExamConstants.INTENT_IS_REEXAM, true);
        context.startActivity(intent);
        AppUtils.startActivityAnimation(context);
    }

    private void showQuestionWithAnswer(int itemPosition) {
        final Archive archive = archiveList.get(itemPosition);
        ExamUserStatus status = new ExamUserStatus() {
        };
        status.UserId = user.getId();
        status.ExamId = archive.getId();
        RetrofitClient.getApiService(context).sendExamUserStatus(status).enqueue(new ApiCallback<ResponseJson>(context,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson result = response.body();
                        if (result != null && !result.IsError) {
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
                        LOG.e("getArchives", " Error " + t.getMessage());
                    }
                }));
    }

    public ArchiveListRecyclerViewAdapter(Context context, List<Archive> items, User user, boolean fromArchive) {
        this.context = context;
        this.archiveList = items;
        this.fromArchive = fromArchive;
        this.user = user;
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
