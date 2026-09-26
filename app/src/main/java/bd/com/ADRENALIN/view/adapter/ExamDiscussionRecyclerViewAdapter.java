package bd.com.ADRENALIN.view.adapter;

/**
 * Created by iqrasys on 8/19/2017.
 */
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.view.activity.ExamDiscussionActivity;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.ExamDiscussion;
import bd.com.ADRENALIN.pojo.ResponseJson;
import bd.com.ADRENALIN.pojo.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/6/17.
 */

public class ExamDiscussionRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ExamDiscussion> bookArrayList;
    private ExamDiscussionActivity context;
    private User user;
    private int examId;
    private RelativeLayout layoutView;

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public class AppReviewViewHolder extends RecyclerView.ViewHolder {
        ExamDiscussion dataModel;
        int position=0;
        @BindView(R.id.recycler_view)
        RecyclerView recyclerView;
        @BindView(R.id.img_recycler_view)
        RecyclerView imgRecyclerView;

        @BindView(R.id.btnReply)
        TextView btnReply;
        @BindView(R.id.btnDelete)
        TextView btnDelete;

        @BindView(R.id.tvAppReviewUserName)
        TextView title;
        @BindView(R.id.tvAppReviewContent)
        TextView tvAppReviewContent;
        @BindView(R.id.tvAppReviewDate)
        TextView tvAppReviewDate;


        public AppReviewViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            setButtonListeners();
        }

        private void setButtonListeners() {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteComment();
                }
            });
        }
        private void onDeleteComment() {
            if(dataModel!=null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
                builder.setMessage("Do you want to delete this discussion?")
                        .setNegativeButton(context.getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                RetrofitClient.getApiService(context).DeleteExamDiscussion(user.getId(), dataModel.Id).enqueue(new ApiCallback<ResponseJson>(context, true,
                                        new Callback<ResponseJson>() {
                                            @Override
                                            public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                                                ResponseJson result = response.body();
                                                if (!result.IsError) {
                                                    position=0;
                                                    for (ExamDiscussion discussion :bookArrayList
                                                         ) {
                                                        if(dataModel.Id==discussion.Id){
                                                            break;
                                                        }
                                                        position++;
                                                    }
                                                    Log.e("SamarukWebSocket","onDeleteComment called  and position is "+position);
                                                    bookArrayList.remove(position);
                                                    notifyItemRemoved(position);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseJson> call, Throwable t) {

                                            }
                                        }));
                            }
                        });
                builder.setNeutralButton(context.getString(R.string.btn_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }else{
                Log.e("SamarukWebSocket","onDeleteComment called  But Data is Null.");
            }

        }
    }

    public ExamDiscussionRecyclerViewAdapter(ExamDiscussionActivity context, List<ExamDiscussion> items, User user, RelativeLayout _layoutView, int examId) {
        this.context = context;
        this.bookArrayList = items;
        this.user=user;
        this.examId=examId;
        layoutView=_layoutView;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        try {
            if (viewType == 0) {
                return new AppReviewViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.card_exam_discussion, parent, false)
                );
            } else if (viewType == 1) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
                return new LoadingViewHolder(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket","ViewHolder  ");
            Log.e("SamarukWebSocket",e.getMessage());
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, position % 2 == 0 ? R.color.white : R.color.white_grayish));
        if (holder instanceof AppReviewViewHolder) {
            try {
                ExamDiscussion book = bookArrayList.get(position);
                AppReviewViewHolder itemHolder=(AppReviewViewHolder)holder;
                itemHolder.title.setText(book.CreatedBy);
                itemHolder.tvAppReviewContent.setText(book.Content);
                itemHolder.tvAppReviewDate.setText(book.CreatedAt);
                //recyclerView
                ExamDiscussionReplyRecyclerViewAdapter adpt=new ExamDiscussionReplyRecyclerViewAdapter(context);
                adpt.Set(user,itemHolder.recyclerView,layoutView,book,examId,itemHolder.btnReply);
                ImageRecyclerViewAdapter imgAdpt=new ImageRecyclerViewAdapter(context,book.Id,itemHolder.imgRecyclerView,0);
                itemHolder.dataModel=book;
                itemHolder.position=position;
                if(!book.IsOnwer){
                    itemHolder.btnDelete.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SamarukWebSocket","From Adapter  ");
                Log.e("SamarukWebSocket",e.getMessage());
            }
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return bookArrayList.get(position) == null ? 1 : 0;
    }

    public void AddItem(ExamDiscussion item) {
        bookArrayList.add(0,item);
        notifyDataSetChanged();
    }
    public void AddItem(ArrayList<ExamDiscussion> list) {
        bookArrayList.addAll(list);
        notifyDataSetChanged();
    }
    public void SetLoading() {
        bookArrayList.add(null);
        notifyItemInserted(bookArrayList.size() - 1);
    }
    public void RemoveLoading() {
        bookArrayList.remove(bookArrayList.size() - 1);
        notifyItemRemoved(bookArrayList.size());
    }
}
