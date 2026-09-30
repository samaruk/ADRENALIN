package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.Archive;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.util.AppUtils;
import bd.com.ADRENALIN.util.ExamSearch;
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
 * Exams whose result is published, latest first, with a search field. After an attempt the list is
 * loaded again, so the option switches from "Perform Exam" to "Re-exam".
 *
 * Created by mahfuz on 7/12/17.
 */

public class ArchiveActivity extends BaseActivity {

    public static final String TAG = ArchiveActivity.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private final List<Archive> allArchives = new ArrayList<>();
    private ArchiveListRecyclerViewAdapter adapter;
    private ExamSearch search;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        ButterKnife.bind(this);

        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArchiveActivity.this.setTitle(getString(R.string.lbl_archive));

        adapter = new ArchiveListRecyclerViewAdapter(context, new ArrayList<Archive>(), getPrefManager().getUserInfo(), true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        search = new ExamSearch(this, new ExamSearch.Listener() {
            @Override
            public void onQuery(String query) {
                applyFilter();
            }
        });

        getArchivesFromApi(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Back from an attempt, the questions or the discussion: the next option may have changed.
        getArchivesFromApi(false);
    }

    private void getArchivesFromApi(boolean showProgress) {
        ExamType examType = getPrefManager().getExamTypeSelected();
        User user = getPrefManager().getUserInfo();

        if (user == null || examType == null) return;
        LOG.e("getArchives", new Gson().toJson(user));
        RetrofitClient.getApiService(context).getArchives(examType.getId(), user.getId()).enqueue(new ApiCallback<List<Archive>>(context, showProgress,
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
        if (archiveList != null && !archiveList.isEmpty()) {
            allArchives.clear();
            allArchives.addAll(archiveList);
            applyFilter();
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }
    }

    /** Shows the exams that match the search field (all of them when it is empty). */
    private void applyFilter() {
        String query = search.query();
        List<Archive> shown = new ArrayList<>();
        for (Archive a : allArchives) {
            Date date = AppUtils.getDateFromString(a.getStartAt());
            String dateText = date == null ? "" : AppUtils.getDateStringFromDate(date);
            if (ExamSearch.matches(query, a.getName(), a.getContent(), a.getStartAt(), dateText)) shown.add(a);
        }
        adapter.setItems(shown);
        search.showResult(shown.size(), allArchives.size());
    }
}
