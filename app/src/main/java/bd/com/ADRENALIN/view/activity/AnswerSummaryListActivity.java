package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.AnswerSummary;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.view.adapter.AnswerSummaryRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/9/17.
 */

public class AnswerSummaryListActivity extends BaseActivity {

    public static final String TAG = AnswerSummaryListActivity.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_summary_list);
        ButterKnife.bind(this);

        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AnswerSummaryListActivity.this.setTitle(getString(R.string.lbl_answer_summary));

        getAnswerSummaryListFromApi();
    }

    private void getAnswerSummaryListFromApi() {
        String userId = getPrefManager().getUserInfo().getId();
        int examType = getPrefManager().getExamTypeSelected().getId();

        RetrofitClient.getApiService(context).getAnswerSummeryList(userId, examType).enqueue(new ApiCallback<List<AnswerSummary>>(context,
                new Callback<List<AnswerSummary>>() {
                    @Override
                    public void onResponse(Call<List<AnswerSummary>> call, Response<List<AnswerSummary>> response) {
                        List<AnswerSummary> answerSummaries = response.body();
                        setDataToAdapter(answerSummaries);
                        //Log.e(TAG, new Gson().toJson(answerSummaries));
                    }

                    @Override
                    public void onFailure(Call<List<AnswerSummary>> call, Throwable t) {
                    }
                }));
    }

    private void setDataToAdapter(List<AnswerSummary> answerSummaryList) {
        if (answerSummaryList != null && !answerSummaryList.isEmpty()) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new AnswerSummaryRecyclerViewAdapter(context, answerSummaryList));
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }
    }
}
