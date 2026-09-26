package bd.com.ADRENALIN.pojo;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import bd.com.ADRENALIN.service.FileRequestBody;

/**
 * Created by iqrasys on 8/29/2017.
 */

public class ImageViewModel implements FileRequestBody.ProgressListener {

    public ProgressBar progressBar;
    public ImageView ing;
    public Bitmap bitmap;
    public boolean IsLoaded;
    public ImageViewModel(Bitmap bitmap){
        IsLoaded=true;
        this.bitmap=bitmap;
    }
    public ImageViewModel(String url){
        IsLoaded=false;
    }
    public final static ArrayList<ImageViewModel> GetList(ArrayList<Bitmap> images){
        ArrayList<ImageViewModel> list=new ArrayList<ImageViewModel>(images.size());
        for (Bitmap item :images
             ) {
            list.add(new ImageViewModel(item));
        }
        return list;
    }
    @Override
    public void onProgress(int percentage) {
        if(progressBar!=null){
            progressBar.setProgress(percentage);
        }
    }
    @Override
    public void onFinish() {
        if(progressBar!=null){
            progressBar.setProgress(100);
            ing.setAlpha(1.0f);
            //progressBar.setVisibility(View.GONE);
        }
    }
}
