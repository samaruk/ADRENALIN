package bd.com.ADRENALIN.view.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Merit;
import bd.com.ADRENALIN.pojo.User;
import bd.com.ADRENALIN.view.adapter.MeritListRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class MeritListFragment extends BaseFragment {

    public static final String TAG = MeritListFragment.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.nesterScrollViewNextExamSection)
    NestedScrollView nesterScrollViewNextExamSection;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_merit_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        getMeritList();

    }


    private void setDataToAdapter(List<Merit> meritList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
        recyclerView.setAdapter(new MeritListRecyclerViewAdapter(context, meritList));

    }

    private void getMeritList() {
        ExamType examType = getPrefManager().getExamTypeSelected();
        User user = getPrefManager().getUserInfo();

        if (user == null || examType == null) return;

        RetrofitClient.getApiService(context)
                .getMeritList(examType.getId(), user.getId()).enqueue(new ApiCallback<List<Merit>>(context,
                new Callback<List<Merit>>() {
                    @Override
                    public void onResponse(Call<List<Merit>> call, Response<List<Merit>> response) {
                        List<Merit> meritList = response.body();

                        if (meritList != null && !meritList.isEmpty()) {
                            LOG.e(TAG, meritList.toString());
                            setDataToAdapter(meritList);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Merit>> call, Throwable t) {
                    }
                }));
    }


}
