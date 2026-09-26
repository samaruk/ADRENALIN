package bd.com.ADRENALIN.view.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.view.adapter.AboutUsListRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class AboutUsFragment extends BaseFragment {

    public static final String TAG = AboutUsFragment.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        getAboutUsList();
    }


    private void setDataToAdapter(List<Integer> imgIds ) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new AboutUsListRecyclerViewAdapter(context, imgIds));

    }

    private void getAboutUsList() {
        RetrofitClient.getApiService(context)
                .getAboutUsList().enqueue(new ApiCallback<List<Integer>>(context,
                new Callback<List<Integer>>() {
                    @Override
                    public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                        List<Integer> imgIds = response.body();

                        if (imgIds != null && !imgIds.isEmpty()) {
                            setDataToAdapter(imgIds);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Integer>> call, Throwable t) {
                    }
                }));
    }


}
