package bd.com.ADRENALIN.view.adapter;

/**
 * Created by iqrasys on 9/5/2017.
 */

        import android.content.Context;
        import android.content.DialogInterface;
        import androidx.viewpager.widget.PagerAdapter;
        import androidx.appcompat.app.AlertDialog;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import java.util.ArrayList;

        import bd.com.ADRENALIN.network.ApiCallback;
        import bd.com.ADRENALIN.network.RetrofitClient;
        import bd.com.ADRENALIN.pojo.ImageModel;
        import bd.com.ADRENALIN.pojo.ResponseJson;
        import bd.com.ADRENALIN.R;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

public class CustomPagerAdapter extends PagerAdapter {
    private boolean isDeletable;
    private Context mContext;
    private ArrayList<ImageModel> list;

    public CustomPagerAdapter(Context context,ArrayList<ImageModel> _list,boolean _isDeletable) {
        mContext = context;
        list=_list;
        isDeletable=_isDeletable;
    }
    private void onDelete(final ImageModel model,final int positopn){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppTheme_Dark_Dialog);
        builder.setMessage("Do you want to delete this Picture?")
                .setNegativeButton(mContext.getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        RetrofitClient.getApiService(mContext).DeleteCommentImage(model.Id).enqueue(new ApiCallback<ResponseJson>(mContext,true,
                                new Callback<ResponseJson>() {
                                    @Override
                                    public void onResponse(Call<ResponseJson> call, Response<ResponseJson> response) {
                                        ResponseJson result = response.body();
                                        if(!result.IsError){
                                            list.remove(positopn);
                                            notifyDataSetChanged();
                                            Log.e("SamarukWebSocket","onDeleteComment called  and success."+list.size());
                                        }else {
                                            Log.e("SamarukWebSocket","onDeleteComment called  But Data is Null.");
                                            Log.e("SamarukWebSocket","onDeleteComment called  But Data is Null."+result.Msg);
                                            Log.e("SamarukWebSocket","onDeleteComment called  But Data is Null."+result.IsError);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseJson> call, Throwable t) {

                                    }
                                }));
                    }
                });
        builder.show();

    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        ViewGroup layout = null;
        try {
            final ImageModel modelObject = list.get(position);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            layout = (ViewGroup) inflater.inflate(R.layout.fragment_full_screen_image_viewer, collection, false);
            modelObject.img=layout.findViewById(R.id.img);
            modelObject.progressBar=layout.findViewById(R.id.progressBar);
            modelObject.SetLarge(mContext);
            collection.addView(layout);
            if(isDeletable){
                layout.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onDelete(modelObject,position);
                    }
                });
            }else {
                layout.findViewById(R.id.btnDelete).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SamarukWebSocket","showFullScreen instantiateItem "+e.getMessage());
        }
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ImageModel customPagerEnum =list.get(position);
        return mContext.getString(R.string.app_name);
    }

}
