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
import bd.com.ADRENALIN.view.activity.BaseActivity;
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
 * Archived (result published) exams. Tapping one offers: Perform Exam (the student's first attempt, which
 * counts in the merit list and the position) or Re-exam (every later attempt, practice only), Show Question
 * and Show Discussion. Looking at the answers or the discussion before the first attempt turns later
 * attempts into re-exams, so the student is asked first.
 *
 * Created by mahfuz on 7/6/17.
 */

public class ArchiveListRecyclerViewAdapter extends
        RecyclerView.Adapter<ArchiveListRecyclerViewAdapter.ArchiveListViewHolder> {

    /** ExamUserStatus of a student who opened the questions with answers or the discussion. */
    private static final String STATUS_SHOW = "Show";

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

    /** "Choose your option." dialog with Perform Exam or Re-exam / Show Question / Show Discussion. */
    private void showOptions(final int itemPosition) {
        if (itemPosition < 0 || itemPosition >= archiveList.size()) return;
        final Archive archive = archiveList.get(itemPosition);
        final boolean firstAttempt = archive.canPerform();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_archive_options, null);
        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                .setView(view)
                .setCancelable(true)
                .create();

        TextView tvInfo = view.findViewById(R.id.tvArchiveInfo);
        String info;
        if (firstAttempt) {
            info = context.getString(R.string.lbl_perform_info);
        } else if (archive.getReExamCount() > 0) {
            info = context.getString(R.string.lbl_reexam_count, archive.getReExamCount());
        } else if (archive.hasAnswer()) {
            info = context.getString(R.string.lbl_answered_info);
        } else {
            info = context.getString(R.string.lbl_viewed_info);
        }
        tvInfo.setText(info);
        tvInfo.setVisibility(View.VISIBLE);

        TextView btnAttempt = view.findViewById(R.id.btnReexam);
        btnAttempt.setText(firstAttempt ? R.string.btn_perform_exam : R.string.btn_reexam);
        btnAttempt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                confirmAttempt(archive, firstAttempt);
            }
        });
        view.findViewById(R.id.btnShowQuestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                warnBeforeViewing(archive, firstAttempt, new Runnable() {
                    @Override
                    public void run() {
                        showQuestionWithAnswer(archive);
                    }
                });
            }
        });
        view.findViewById(R.id.btnShowDiscussion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                warnBeforeViewing(archive, firstAttempt, new Runnable() {
                    @Override
                    public void run() {
                        if (firstAttempt) recordViewed(archive);
                        Intent intent = new Intent(context, ExamDiscussionActivity.class);
                        intent.putExtra(AppConstants.ExamConstants.INTENT_EXAM_ID, archive.getId());
                        context.startActivity(intent);
                        AppUtils.startActivityAnimation(context);
                    }
                });
            }
        });
        dialog.show();
    }

    /**
     * Before the first attempt, looking at the answers or the discussion would let a student learn the
     * answers and then score in the merit list, so it makes every later attempt a re-exam. Ask first.
     */
    private void warnBeforeViewing(final Archive archive, boolean firstAttempt, final Runnable open) {
        if (!firstAttempt) {
            open.run();
            return;
        }
        new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                .setMessage(context.getString(R.string.lbl_view_before_perform))
                .setPositiveButton(context.getString(R.string.btn_continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        archive.markViewedAnswers();
                        open.run();
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
    }

    /** Tells the server that the student saw the answers (or the discussion) of this exam. */
    private void recordViewed(Archive archive) {
        if (user == null) return;
        ExamUserStatus status = new ExamUserStatus() {
        };
        status.UserId = user.getId();
        status.ExamId = archive.getId();
        status.Status = STATUS_SHOW;
        RetrofitClient.getApiService(context).sendExamUserStatus(status).enqueue(new Callback<ResponseJson>() {
            @Override
            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
            }

            @Override
            public void onFailure(Call<ResponseJson> call, Throwable t) {
                LOG.e("recordViewed", t.getMessage());
            }
        });
    }

    /** Confirms, then starts or resumes the first attempt ("Perform Exam") or a re-exam. */
    private void confirmAttempt(final Archive archive, final boolean firstAttempt) {
        final boolean isReExam = !firstAttempt;
        boolean resuming = user != null && ((BaseActivity) context).getPrefManager()
                .getExamDraft(user.getId(), archive.getId(), isReExam) != null;
        String message;
        if (firstAttempt) {
            message = resuming ? context.getString(R.string.lbl_perform_resume)
                    : context.getString(R.string.lbl_perform_confirmation, archive.getD());
        } else {
            message = resuming ? context.getString(R.string.lbl_reexam_resume)
                    : context.getString(R.string.lbl_reexam_confirmation, archive.getD());
        }
        new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog)
                .setMessage(message)
                .setPositiveButton(context.getString(resuming ? R.string.btn_continue : R.string.btn_start), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startAttempt(archive, isReExam);
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
    }

    /**
     * Starts (or resumes) an attempt with the same questions and duration. The first attempt is stored as the
     * student's exam (merit list and position); a re-exam never enters the merit list.
     */
    private void startAttempt(Archive archive, boolean isReExam) {
        Intent intent = new Intent(context, ExamActivity.class);
        Exam exam = new Exam();
        exam.setId(archive.getId());
        exam.setName(archive.getName());
        exam.setDuration(archive.getD());
        exam.setCategoryId(archive.getCategoryId());
        exam.setTotalQuestion(archive.getTotalQuestion());
        exam.setReExam(isReExam);
        intent.putExtra(AppConstants.ExamConstants.INTENT_CODE, new Gson().toJson(exam));
        intent.putExtra(AppConstants.ExamConstants.INTENT_IS_REEXAM, isReExam);
        intent.putExtra(AppConstants.ExamConstants.INTENT_FROM_ARCHIVE, true);
        context.startActivity(intent);
        AppUtils.startActivityAnimation(context);
    }

    private void showQuestionWithAnswer(final Archive archive) {
        ExamUserStatus status = new ExamUserStatus() {
        };
        status.UserId = user.getId();
        status.ExamId = archive.getId();
        status.Status = STATUS_SHOW;
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

    /** Replaces the shown exams (search results). */
    public void setItems(List<Archive> items) {
        this.archiveList = items;
        notifyDataSetChanged();
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
