package bd.com.ADRENALIN.view.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.network.ApiCallback;
import bd.com.ADRENALIN.network.RetrofitClient;
import bd.com.ADRENALIN.pojo.Book;
import bd.com.ADRENALIN.pojo.ExamType;
import bd.com.ADRENALIN.view.adapter.BookListRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mahfuz on 7/7/17.
 */

public class BookListActivity extends BaseActivity {

    public static final String TAG = BookListActivity.class.getSimpleName();
    private Context context;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        ButterKnife.bind(this);

        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BookListActivity.this.setTitle(getString(R.string.lbl_book_list));

        getBookListFromApi();
    }

    private void getBookListFromApi() {
        ExamType examTypeSelected = getPrefManager().getExamTypeSelected();
        if (examTypeSelected == null) {
            showMsg("No Next Exam Info");
            return;
        }

        RetrofitClient.getApiService(context).getBookList(examTypeSelected.getId()).enqueue(new ApiCallback<List<Book>>(context,
                new Callback<List<Book>>() {
                    @Override
                    public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                        List<Book> bookList = response.body();
//                        LOG.e(TAG, bookList.toString());
                        setDataToAdapter(bookList);
                    }

                    @Override
                    public void onFailure(Call<List<Book>> call, Throwable t) {
                    }
                }));
    }

    private void setDataToAdapter(List<Book> bookList) {
        if (bookList != null && !bookList.isEmpty()) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new BookListRecyclerViewAdapter(context, bookList));
        } else {
            showMsg(getString(R.string.lbl_no_data));
        }
    }

}
