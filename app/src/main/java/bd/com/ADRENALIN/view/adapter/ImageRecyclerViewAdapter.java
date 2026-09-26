package bd.com.ADRENALIN.view.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.util.ArrayList;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ImageModel;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.pojo.ResponseJsonList;
import bd.com.ADRENALIN.util.AppConstants;
import bd.com.ADRENALIN.view.activity.BaseActivity;
import bd.com.ADRENALIN.view.activity.FullScreenImageViewerActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iqrasys on 8/29/2017.
 */

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {
    private boolean isDeletable=false;
    private String url="ExamDiscussion";
    private ImageRecyclerViewAdapter that;
    public ArrayList<ImageModel> galleryList;
    private BaseActivity context;
    private int Id;
    public ImageRecyclerViewAdapter(BaseActivity context, int Id,RecyclerView recyclerView,int from) {
        this.Id=Id;
        this.context = context;
        that=this;
        setData(recyclerView);
    }
    public ImageRecyclerViewAdapter(BaseActivity context, ArrayList<Integer> list,RecyclerView recyclerView,String _url) {
        //url="Question";
        url=_url;
        this.context = context;
        that=this;
        galleryList=new ArrayList<ImageModel>();
        for (Integer id : list) {
            galleryList.add(new ImageModel(id,url));
        }
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(that);
    }
    public void setDataList(ArrayList<Integer> list){
        galleryList=new ArrayList<ImageModel>();
        for (Integer id : list) {
            galleryList.add(new ImageModel(id,url));
        }
        notifyDataSetChanged();

    }
    private void setData(final RecyclerView recyclerView){

        try {
            RetrofitClient.getApiService(context).GetCommentImageId(Id).enqueue(new ApiCallback<ResponseJsonList<Integer>>(context,false,
                    new Callback<ResponseJsonList<Integer>>() {
                        @Override
                        public void onResponse(Call<ResponseJsonList<Integer>> call, Response<ResponseJsonList<Integer>> response) {
                            ResponseJsonList<Integer> list = response.body();
                            galleryList=new ArrayList<ImageModel>();
                            try {
                                for (Integer id : list.Data) {
                                    galleryList.add(new ImageModel(id));
                                }
//                                final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,3);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(that);
                                } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("SamarukWebSocket",e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseJsonList<Integer>> call, Throwable t) {

                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket","ImageRecyclerViewAdapter GetCommentImageId  Error "+e.getMessage());
        }

    }
    private void showFullScreen(int i){
        try {
            ArrayList<Integer> idList=new ArrayList<Integer>();
            for (ImageModel id:galleryList) {
                idList.add(id.Id);
            }
            Log.e("SamarukWebSocket","showFullScreen  idList "+new Gson().toJson(idList));
            Intent intent = new Intent(context, FullScreenImageViewerActivity.class);
            intent.putExtra(AppConstants.ExamDiscussionConstants.EXAM_DISCUSSION_IMAGE_POSITION, i);
            intent.putExtra(AppConstants.ExamDiscussionConstants.EXAM_DISCUSSION_IMAGE_URL, url);
            intent.putExtra(AppConstants.ExamDiscussionConstants.EXAM_DISCUSSION_IMAGE_IS_EDITABLE, isDeletable);
            intent.putExtra(AppConstants.ExamDiscussionConstants.EXAM_DISCUSSION_ALL_IMAGES, new Gson().toJson(idList));
            context.startActivity(intent);
            AppUtils.startActivityAnimation(context);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket","showFullScreen  Error "+e.getMessage());
        }
    }
    @Override
    public ImageRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_comment_image, viewGroup, false);
        return new ImageRecyclerViewAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ImageRecyclerViewAdapter.ViewHolder viewHolder, final int i) {
        try {
            //viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageModel data = galleryList.get(i);
            Log.e("SamarukWebSocket","ImageRecyclerViewAdapter data.Id  "+data.Id);
            data.img = viewHolder.img;
            data.progressBar=viewHolder.progressBar;
            if(data.bitmap!=null) {
                data.Set(viewHolder.img);
            }else if(data.Id!=0){
                data.Set(context);
            }
            data.img .setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showFullScreen(i);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket","ImageRecyclerViewAdapter onBindViewHolder  Error "+e.getMessage());
        }
    }
    @Override
    public int getItemCount() {
        return galleryList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Log.e("SamarukWebSocket", "ViewHolder  ");
        }
    }
    public void AddItem(ImageModel item) {
        galleryList.add(0, item);
        notifyItemInserted(0);
    }
    public ArrayList<Bitmap> GetImages() {
        ArrayList<Bitmap> images = new ArrayList<Bitmap>(galleryList.size());
        for (ImageModel item : galleryList) {
            images.add(item.bitmap);
        }
        return images;
    }
}
