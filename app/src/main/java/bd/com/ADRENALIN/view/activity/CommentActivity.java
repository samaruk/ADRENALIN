package bd.com.ADRENALIN.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.service.FileRequestBody;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.AppReview;
import bd.com.ADRENALIN.pojo.PostModel.bd.com.dvec.pojo.PostModel.AppReviewPostModel;
import bd.com.ADRENALIN.pojo.ResponseJson;
import bd.com.ADRENALIN.pojo.UploadModel;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.view.adapter.UploadedImageRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iqrasys on 8/27/2017.
 */

public class CommentActivity extends BaseActivity {

    public static final String TAG = AppReviewActivity.class.getSimpleName();
    private Context context;
    private User user;
    private static int RESULT_LOAD_IMG = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private File sourceFile;

    private UploadedImageRecyclerViewAdapter adpt;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtComment)
    EditText txtComment;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.btnUpload)
    Button btnUpload;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        context = this;
        user = getPrefManager().getUserInfo();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CommentActivity.this.setTitle(getString(R.string.btn_discussion));
        setButtonListeners();
        setDataToAdapter();
    }
    private void setButtonListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createReview();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadImagefromGallery();
            }
        });
    }
    private void setDataToAdapter() {
        adpt=new UploadedImageRecyclerViewAdapter(context, new ArrayList<UploadModel>());
        //recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adpt);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                txtComment.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }
    private void createReview() {
        final String content=txtComment.getText().toString();
        if (content.isEmpty()) {
            return;
        }

        btnSave.setEnabled(false);

        User user = getPrefManager().getUserInfo();
        AppReviewPostModel review = new AppReviewPostModel();
        review.Content=content;
        review.CreatedBy=user.getId();
        RetrofitClient.getApiService(context).createAppReview(review).enqueue(new ApiCallback<ResponseJson>(context,
                new Callback<ResponseJson>() {
                    @Override
                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                        ResponseJson result = response.body();
                        btnSave.setEnabled(true);
                        if (result != null && !result.IsError){
                            txtComment.setText("");
                            AppReview item =new AppReview();
                            item.IsActive=true;
                            item.Content=content;
                            item.CreatedAt= AppUtils.getSlashSeparatedDateStringFromDate(Calendar.getInstance().getTime());
                            item.CreatedBy="Your Review";
                            item.Id=result.Id;
                        }
                        else{
                            showMsg(result.Msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseJson> call, Throwable t) {

                    }
                }));

    }
    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                UploadModel model=new UploadModel(bitmap);
                adpt.AddItem(model);
                final File  file =sourceFile= new File(getRealPathFromURI(context,selectedImage));
                RequestBody reqFile = new FileRequestBody( file,model);
                MultipartBody.Part body = MultipartBody.Part.createFormData("Image", file.getName(), reqFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
                RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), user.getId());
                RetrofitClient.getApiService(context).postImage(body, name,userId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //file.delete();
                        Gson gson = new Gson();
                        // Do Something
                        String res= "";
                        try {
                            res = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("SamarukWebSocket","Upload Successfully Bur Error Response  "+e.getMessage());
                        }
                        Log.e("SamarukWebSocket","Upload Successfully  "+res);
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //file.delete();
                        t.printStackTrace();
                        Log.e("SamarukWebSocket","Upload Fail  ");
                        Log.e("SamarukWebSocket",t.getMessage());
                    }
                });
                //file.delete();
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("SamarukWebSocket",e.getMessage());
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
