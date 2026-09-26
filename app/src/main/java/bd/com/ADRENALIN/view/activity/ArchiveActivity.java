package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.Archive;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.util.LOG;
import bd.com.ADRENALIN.view.adapter.ArchiveListRecyclerViewAdapter;
import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/12/17.
 */

public class ArchiveActivity extends BaseActivity {

    public static final String TAG = ArchiveActivity.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        ButterKnife.bind(this);

        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArchiveActivity.this.setTitle(getString(R.string.lbl_archive));

        getArchivesFromApi();
    }

    private void getArchivesFromApi() {
        ExamType examType = getPrefManager().getExamTypeSelected();
        User user = getPrefManager().getUserInfo();

        if (user == null || examType == null) return;
        LOG.e("getArchives", new Gson().toJson(user));
        RetrofitClient.getApiService(context).getArchives(examType.getId(), user.getId()).enqueue(new ApiCallback<List<Archive>>(context,
                new Callback<List<Archive>>() {
                    @Override
                    public void onResponse(Call<List<Archive>> call, Response<List<Archive>> response) {
                        List<Archive> archiveList = response.body();
                        LOG.e("getArchives", new Gson().toJson(archiveList));
                        setDataToAdapter(archiveList);
                    }

                    @Override
                    public void onFailure(Call<List<Archive>> call, Throwable t) {
                        LOG.e("getArchives", " Error "+t.getMessage());

                    }
                }));
    }

    private void setDataToAdapter(List<Archive> archiveList) {
        User user = getPrefManager().getUserInfo();
        if (archiveList != null && !archiveList.isEmpty()) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new ArchiveListRecyclerViewAdapter(context, archiveList,user, true));
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }
    }
}
