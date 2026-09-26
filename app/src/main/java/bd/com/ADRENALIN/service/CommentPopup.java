package bd.com.ADRENALIN.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.UploadModel;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.view.activity.BaseActivity;
import bd.com.ADRENALIN.view.adapter.UploadedImageRecyclerViewAdapter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by iqrasys on 8/28/2017.
 */

public class CommentPopup {
    private View view;
    private View content;
    private BaseActivity context;
    private UploadedImageRecyclerViewAdapter adpt;
    private User user;
    private PopupWindow pwindow;
    private CommentSaveListener saver;
    private String identifier;

    ///
    private static int RESULT_LOAD_IMG = 1;

    ///
    private RecyclerView recyclerView;
    private EditText txtComment;
    private Button btnSave;
    private Button btnUpload;
    private Button btnCancel;

    public interface CommentSaveListener {
        void onSave(ArrayList<Bitmap> images, String text,String identifier);
    }
    public CommentPopup(BaseActivity context,User user,CommentSaveListener saver, View view, View content){

        this.view=view;
        this.context=context;
        this.content=content;
        this.user=user;
        this.saver=saver;
        identifier=UUID.randomUUID().toString();
        onCreate();
    }
    private void onCreate(){

        try {
            pwindow = new PopupWindow(content, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
            ViewParent pa=view.getParent();
            pwindow.showAtLocation(view, Gravity.CENTER,0,0);
            recyclerView = (RecyclerView) content.findViewById(R.id.recycler_view);
            txtComment = (EditText) content.findViewById(R.id.txtComment);
            btnSave = (Button) content.findViewById(R.id.btnSave);
            btnUpload = (Button) content.findViewById(R.id.btnUpload);
            btnCancel = (Button) content.findViewById(R.id.btnCancel);
            recyclerView = (RecyclerView) content.findViewById(R.id.recycler_view);
            recyclerView = (RecyclerView) content.findViewById(R.id.recycler_view);

            setButtonListeners();
            setDataToAdapter();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket","Upload Fail  ");
            Log.e("SamarukWebSocket",e.getMessage());
        }
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
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dismiss();
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
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }
    private void createReview() {
        final String text=txtComment.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        saver.onSave(adpt.GetImages(),text,identifier);
    }
    public void Dismiss(){
        pwindow.dismiss();
    }
    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        context.startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == context.RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                UploadModel model=new UploadModel(bitmap);
                adpt.AddItem(model);
                final File file = new File(getRealPathFromURI(context,selectedImage));
                RequestBody reqFile = new FileRequestBody( file,model);
                MultipartBody.Part body = MultipartBody.Part.createFormData("Image", file.getName(), reqFile);
                RequestBody key = RequestBody.create(MediaType.parse("text/plain"), identifier);
                RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), user.getId());
                RetrofitClient.getApiService(context).postImage(body, key,userId).enqueue(new Callback<ResponseBody>() {
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

            }
        } catch (Exception e) {
            Log.e("SamarukWebSocket",e.getMessage());

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
