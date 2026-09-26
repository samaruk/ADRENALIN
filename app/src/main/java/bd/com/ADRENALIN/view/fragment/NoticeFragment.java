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

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.view.adapter.NoticeListRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class NoticeFragment extends BaseFragment {

    public static final String TAG = NoticeFragment.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notice_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        getNoticeList();
    }


    private void setDataToAdapter(List<String> meritList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new NoticeListRecyclerViewAdapter(context, meritList));

    }

    private void getNoticeList() {
        ExamType examType = getPrefManager().getExamTypeSelected();
        User user = getPrefManager().getUserInfo();

        if (user == null || examType == null) return;

        RetrofitClient.getApiService(context)
                .getNotices().enqueue(new ApiCallback<List<String>>(context,
                new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        List<String> meritList = response.body();

                        if (meritList != null && !meritList.isEmpty()) {
                            LOG.e(TAG, meritList.toString());
                            setDataToAdapter(meritList);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                    }
                }));
    }


}
