package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.Book;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mahfuz on 7/6/17.
 */

public class BookListRecyclerViewAdapter extends
        RecyclerView.Adapter<BookListRecyclerViewAdapter.BookListViewHolder> {

    private List<Book> bookArrayList;
    private Context context;

    public class BookListViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.cv)
        CardView cardView;
        @BindView(R.id.tvBookTitle)
        TextView title;
        @BindView(R.id.tvBookSubject)
        TextView tvBookSubject;
//        @BindView(R.id.tvSyllabusContent)
//        TextView content;


        public BookListViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public BookListRecyclerViewAdapter(Context context, List<Book> items) {
        this.context = context;
        this.bookArrayList = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BookListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_book, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(BookListViewHolder holder, int position) {
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, position % 2 == 0 ? R.color.white : R.color.white_grayish));

        Book book = bookArrayList.get(position);
        holder.title.setText(book.getName());
        holder.tvBookSubject.setText(book.getSubject());
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }
}
