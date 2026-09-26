package bd.com.ADRENALIN.view.adapter;

/**
 * Created by iqrasys on 8/20/2017.
 */
import android.content.DialogInterface;
import android.graphics.Bitmap;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamDiscussion;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.pojo.ImageModel;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.ExamDiscussionPostModel;
import bd.com.ADRENALIN.pojo.ResponseJson;
import bd.com.ADRENALIN.pojo.ResponseJsonList;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.service.CommentPopup;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.view.activity.ExamDiscussionActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/6/17.
 */

public class ExamDiscussionReplyRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder>implements CommentPopup.CommentSaveListener {

    private List<ExamDiscussion> bookArrayList;
    private ExamDiscussionActivity context;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount,pageNumber=0;
    private User user;
    private int examId;
    private CommentPopup popup;
    RelativeLayout layoutView;
    TextView btnReply;
    ExamDiscussion discussion;

    @Override
    public void onSave(final ArrayList<Bitmap> images, String text, String identifier) {
        final String content=text;
        ExamDiscussionPostModel review = new ExamDiscussionPostModel();
        ExamType type= context.getPrefManager().getExamTypeSelected();
        review.Content=content;
        review.ExamId=examId;
        review.ParentId=discussion.Id;
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
                        if (result != null && !result.IsError){
                            ExamDiscussion item =new ExamDiscussion(result.Id,content
                                    ,AppUtils.getSlashSeparatedDateStringFromDate(Calendar.getInstance().getTime()),
                                    user.getName(), ImageModel.GetList(images));
                            AddItem(item);
                            popup.Dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {

                    }
                }));
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }
    private class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        public Button btnLoadMore;
        public LoadMoreViewHolder(View view) {
            super(view);
            btnLoadMore = (Button) view.findViewById(R.id.btnLoadMore);
            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isLoading){
                        isLoading=true;
                        RemoveLoading();
                        onLoadMore();
                    }
                }
            });
        }
    }

    public class AppReviewViewHolder extends RecyclerView.ViewHolder {
        ExamDiscussion dataModel;
        int position;
        @BindView(R.id.cv)
        CardView cardView;
        @BindView(R.id.inner_img_recycler_view)
        RecyclerView imgRecyclerView;
        @BindView(R.id.tvAppReviewUserName)
        TextView title;
        @BindView(R.id.tvAppReviewContent)
        TextView tvAppReviewContent;
        @BindView(R.id.tvAppReviewDate)
        TextView tvAppReviewDate;
        @BindView(R.id.btnDelete)
        TextView btnDelete;


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

    public ExamDiscussionReplyRecyclerViewAdapter(ExamDiscussionActivity context) {
        this.context = context;
        this.bookArrayList = new ArrayList<ExamDiscussion>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            return new AppReviewViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_exam_discussion_reply, parent, false)
            );
        } else if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }else  if(viewType==2){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);
            return new LoadMoreViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, position % 2 == 0 ? R.color.white : R.color.white_grayish));
        if (holder instanceof AppReviewViewHolder) {
            ExamDiscussion book = bookArrayList.get(position);
            AppReviewViewHolder itemHolder=(AppReviewViewHolder)holder;
            itemHolder.title.setText(book.CreatedBy);
            itemHolder.tvAppReviewContent.setText(book.Content);
            itemHolder.tvAppReviewDate.setText(book.CreatedAt);
            ImageRecyclerViewAdapter imgAdpt=new ImageRecyclerViewAdapter(context,book.Id,itemHolder.imgRecyclerView,0);
            itemHolder.dataModel=book;
            if(!book.IsOnwer){
                itemHolder.btnDelete.setVisibility(View.GONE);
            }
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else if (holder instanceof LoadMoreViewHolder) {
            //LoadMoreViewHolder loadingViewHolder = (LoadMoreViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return bookArrayList.get(position) == null ? isLoading?1:2 : 0;
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
    public void Set(User _user, RecyclerView _recyclerView,RelativeLayout _layoutView,ExamDiscussion _discussion, int _examId, TextView _btnReply) {
        user=_user;
        recyclerView=_recyclerView;
        examId=_examId;
        btnReply=_btnReply;
        discussion=_discussion;
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(this);
        layoutView=_layoutView;
        onLoadMore();
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.popup_comment, layoutView, false);
                popup=new CommentPopup(context,user,ExamDiscussionReplyRecyclerViewAdapter.this,layoutView,view);
                context.popup =popup;
            }
        });
    }
    public void onLoadMore() {
        pageNumber++;
        SetLoading();
        RetrofitClient.getApiService(context).getExamDiscussion(user.getId(),examId,discussion.Id,5,pageNumber).enqueue(new ApiCallback<ResponseJsonList<ArrayList<Object>>>(context,false,
                new Callback<ResponseJsonList<ArrayList<Object>>>() {
                    @Override
                    public void onResponse(Call<ResponseJsonList<ArrayList<Object>>> call, Response<ResponseJsonList<ArrayList<Object>>> response) {
                        ResponseJsonList<ArrayList<Object>> list = response.body();
                        ArrayList<ExamDiscussion> reviewList=new ArrayList<ExamDiscussion>();
                        for (ArrayList<Object> arr : list.Data) {
                            reviewList.add(ExamDiscussion.get(arr));
                        }
                        isLoading = reviewList.size()!=5;
                        RemoveLoading();
                        AddItem(reviewList);
                        if(!isLoading) {
                            SetLoading();
                        }
                        //Gson gson = new Gson();
                        //Log.e("Samaruk Hossain.",gson.toJson(reviewList));
                    }

                    @Override
                    public void onFailure(Call<ResponseJsonList<ArrayList<Object>>> call, Throwable t) {

                    }
                }));
    }
}

