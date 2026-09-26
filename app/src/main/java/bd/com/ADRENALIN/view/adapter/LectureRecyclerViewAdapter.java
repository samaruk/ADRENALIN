package bd.com.ADRENALIN.view.adapter;

import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.Lecture;
import bd.com.ADRENALIN.pojo.ResponseModel.QuestionExplanation;
import bd.com.ADRENALIN.pojo.ResponseModel.ResponseJsonGeneric;
import bd.com.ADRENALIN.view.activity.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iqrasys on 9/8/2017.
 */

public class LectureRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Lecture> lectureList;
    private BaseActivity mContext;

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }
    public class AppViewHolder extends RecyclerView.ViewHolder {
        public Lecture lecture;
        boolean isLoaded=false,isOpened=false;
        private ImageRecyclerViewAdapter imgAdpt;
        private int clickedId=0;

        @BindView(R.id.cv)
        CardView cardView;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvPublishedAt)
        TextView tvPublishedAt;
        @BindView(R.id.tvContent)
        ExpandableTextView tvContent;
        @BindView(R.id.img_recycler_view)
        RecyclerView imgRecyclerView;

        public AppViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            tvContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggle();
                    tvContent.toggle();
                }
            });
        }

        private void load(final Lecture lecture){
            if(lecture.Details!=null){
                tvContent.setText(lecture.Details.Explanation);
                if(imgAdpt==null)
                imgAdpt=new ImageRecyclerViewAdapter(mContext,lecture.Details.Images,imgRecyclerView,"Lecture");
                else {
                    imgAdpt.setDataList(lecture.Details.Images);
                }
            }else {
                if(imgAdpt!=null){
                    imgAdpt.setDataList(new ArrayList<Integer>());
                }
                isLoaded=true;
                RetrofitClient.getApiService(mContext).getLectureDetail(lecture.Id).enqueue(new ApiCallback< ResponseJsonGeneric<QuestionExplanation>>(mContext,
                        new Callback<ResponseJsonGeneric<QuestionExplanation>>() {
                            @Override
                            public void onResponse(Call< ResponseJsonGeneric<QuestionExplanation>> call, Response< ResponseJsonGeneric<QuestionExplanation>> response) {
                                ResponseJsonGeneric<QuestionExplanation> result = response.body();
                                if (!result.IsError){
                                    lecture.Details=result.Data;
                                    tvContent.setText(lecture.Details.Explanation);
                                    if(imgAdpt==null)
                                        imgAdpt=new ImageRecyclerViewAdapter(mContext,lecture.Details.Images,imgRecyclerView,"Lecture");
                                    else {
                                        imgAdpt.setDataList(lecture.Details.Images);
                                    }
                                }
                                else{

                                }
                            }
                            @Override
                            public void onFailure(Call< ResponseJsonGeneric<QuestionExplanation>> call, Throwable t) {

                            }
                        }));
            }
        }
        private void toggle(){
            try {
                if(isOpened){
                    isOpened=lecture.IsOpened=false;
                    imgRecyclerView.setVisibility(View.GONE);
                }else {
                    isOpened=lecture.IsOpened=true;
                    imgRecyclerView.setVisibility(View.VISIBLE);
                    if(clickedId!=lecture.Id)
                    load(lecture);
                }
                clickedId=lecture.Id;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SamarukWebSocket","Error in RoutineViewHolder is :-  "+e.getMessage());
            }
        }
        public void Hide(){
            if(isOpened){
                tvContent.toggle();
            }
            isOpened=lecture.IsOpened=false;
            imgRecyclerView.setVisibility(View.GONE);
        }
        public void Show(){
            if(!isOpened){
                tvContent.toggle();
            }
            isOpened=lecture.IsOpened=true;
            imgRecyclerView.setVisibility(View.VISIBLE);
            load(lecture);
        }

    }

    public LectureRecyclerViewAdapter(BaseActivity context, List<Lecture> items) {
        mContext = context;
        lectureList = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        try {
            if (viewType == 0) {
                return new LectureRecyclerViewAdapter.AppViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.card_lecture, parent, false)
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
    public void onBindViewHolder(final RecyclerView.ViewHolder hl, int position) {

        if (hl instanceof AppViewHolder) {
            try {
                AppViewHolder holder=(AppViewHolder)hl;
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, position % 2 == 0 ? R.color.white : R.color.white_grayish));

                Lecture lecture = lectureList.get(position);
                holder.tvTitle.setText(lecture.Title);
                holder.tvPublishedAt.setText(lecture.PublishedAt);
                holder.tvContent.setText(lecture.Content);
                if(holder.lecture!=null&&holder.lecture.Id!=lecture.Id){
                    holder.lecture=lecture;
                    if(lecture.IsOpened){
                        holder.Show();
                    }else {
                        holder.Hide();
                    }
                }
                holder.lecture=lecture;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SamarukWebSocket","From Adapter  ");
                Log.e("SamarukWebSocket",e.getMessage());
            }
        } else if (hl instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) hl;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return lectureList.get(position) == null ? 1 : 0;
    }

    public void AddItem(Lecture item) {
        lectureList.add(0,item);
        notifyDataSetChanged();
    }
    public void AddItem(ArrayList<Lecture> list) {
        lectureList.addAll(list);
        notifyDataSetChanged();
    }
    public void SetLoading() {
        lectureList.add(null);
        notifyItemInserted(lectureList.size() - 1);
    }
    public void RemoveLoading() {
        lectureList.remove(lectureList.size() - 1);
        notifyItemRemoved(lectureList.size());
    }
}
