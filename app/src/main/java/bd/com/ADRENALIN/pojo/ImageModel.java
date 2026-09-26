package bd.com.ADRENALIN.pojo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.service.FileRequestBody;

/**
 * Created by iqrasys on 9/5/2017.
 */

public class ImageModel implements FileRequestBody.ProgressListener {
    public ImageView img;
    public Bitmap bitmap;
    public ProgressBar progressBar;
    public int Id;
    public String Url="ExamDiscussion";
    public ImageModel(Bitmap bitmap){
        this.bitmap=bitmap;
        Id=0;
    }
    public ImageModel(int Id){
        this.Id=Id;
    }
    public ImageModel(int Id,String url){
        this.Url=url;
        this.Id=Id;
    }
    public void Set(Context context){
        Set(context,Id);
    }
    public void Set(Context context, int id){
        Set(context, RetrofitClient.BASE_URL+Url+"/SmallImage?Id="+Id);
    }

    public void Set(Context context,String url){
        try {

            Glide.with(context)
                    .load(url )
                    //.thumbnail(0.26f).crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if(progressBar!=null)
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(img);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket","ImageModel Set  Error in Ex "+e.getMessage());
        }
    }
    public void SetLarge(Context context){
        Set(context,RetrofitClient.BASE_URL+Url+"/LargeImage?Id="+Id);
    }
    public void SetOrginal(Context context){
        Set(context,RetrofitClient.BASE_URL+Url+"/OrginalImage?Id="+Id);
    }
    public void Set(ImageView view){
        img=view;
        img.setImageBitmap(bitmap);
//        progressBar.setVisibility(View.GONE);
    }
    public final static ArrayList<ImageModel> GetList(ArrayList<Bitmap> images){
        ArrayList<ImageModel> list=new ArrayList<ImageModel>(images.size());
        for (Bitmap item :images
                ) {
            list.add(new ImageModel(item));
        }
        return list;
    }
    @Override
    public void onProgress(int percentage) {
        /*if(progressBar!=null){
            progressBar.setProgress(percentage);
            //btnProgress.setWidth(width*percentage/100);
            //btnProgress.getL
        }*/
    }
    @Override
    public void onFinish() {
       /* if(progressBar!=null){
            progressBar.setProgress(100);
            ing.setAlpha(1.0f);
            //progressBar.setVisibility(View.GONE);
        }*/
    }
}
