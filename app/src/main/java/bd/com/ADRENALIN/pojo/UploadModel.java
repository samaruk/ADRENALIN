package bd.com.ADRENALIN.pojo;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import bd.com.ADRENALIN.service.FileRequestBody;

/**
 * Created by iqrasys on 8/28/2017.
 */

public class UploadModel implements FileRequestBody.ProgressListener {

    public ProgressBar progressBar;
    public ImageView ing;
    public Button btnProgress;
    public Bitmap bitmap;
    public int width=100;
    public UploadModel(Bitmap bitmap){
        this.bitmap=bitmap;
    }
    @Override
    public void onProgress(int percentage) {
        if(progressBar!=null){
            progressBar.setProgress(percentage);
            //btnProgress.setWidth(width*percentage/100);
            //btnProgress.getL
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
