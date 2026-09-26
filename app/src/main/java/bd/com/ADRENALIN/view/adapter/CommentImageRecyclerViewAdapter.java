package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.UploadModel;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by iqrasys on 9/5/2017.
 */


public class CommentImageRecyclerViewAdapter extends RecyclerView.Adapter<CommentImageRecyclerViewAdapter.ViewHolder> {
    private ArrayList<UploadModel> galleryList;
    private Context context;

    public CommentImageRecyclerViewAdapter(Context context, ArrayList<UploadModel> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public CommentImageRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_uploaded_image, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentImageRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        UploadModel model = galleryList.get(i);
        model.ing = viewHolder.img;
        model.progressBar = viewHolder.btnProgressBar;
        viewHolder.img.setImageBitmap((model.bitmap));
        Log.e("SamarukWebSocket", "Width : " + model.width);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.btnProgressBar)
        ProgressBar btnProgressBar;
        //@BindView(R.id.btnProgress)
        //Button btnProgress;
        int width = 0;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Log.e("SamarukWebSocket", "ViewHolder  ");
            width = view.getWidth();
        }
    }

    public void AddItem(UploadModel item) {
        galleryList.add(0, item);
        notifyItemInserted(0);
    }

    public ArrayList<Bitmap> GetImages() {
        ArrayList<Bitmap> images = new ArrayList<Bitmap>(galleryList.size());
        for (UploadModel item : galleryList) {
            images.add(item.bitmap);
        }
        return images;
    }
}
