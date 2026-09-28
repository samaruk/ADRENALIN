package bd.com.ADRENALIN.view.adapter.content;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.util.ContentUi;

/**
 * Tappable rows (picture or letter, title, subtitle, chevron) with optional section headings:
 * the courses of Course Plan and the subjects of Lectures.
 */
public class RowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class Row {
        public final boolean isSection;
        public final String title;
        public final String subtitle;
        public final String imageUrl;
        public final Object tag;

        private Row(boolean isSection, String title, String subtitle, String imageUrl, Object tag) {
            this.isSection = isSection;
            this.title = title;
            this.subtitle = subtitle;
            this.imageUrl = imageUrl;
            this.tag = tag;
        }

        public static Row section(String title) {
            return new Row(true, title, null, null, null);
        }

        /** A row; without an image the first letter of the title is shown on the banner colour. */
        public static Row item(String title, String subtitle, String imageUrl, Object tag) {
            return new Row(false, title, subtitle, imageUrl, tag);
        }
    }

    public interface OnRowClick {
        void onRow(Row row);
    }

    private static final int TYPE_SECTION = 1, TYPE_ROW = 2;
    private final List<Row> rows;
    private final OnRowClick listener;

    public RowAdapter(List<Row> rows, OnRowClick listener) {
        this.rows = rows;
        this.listener = listener;
    }

    static class Holder extends RecyclerView.ViewHolder {
        Holder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).isSection ? TYPE_SECTION : TYPE_ROW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == TYPE_SECTION ? R.layout.item_cc_section : R.layout.item_cc_row;
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Row row = rows.get(position);
        View view = holder.itemView;
        if (row.isSection) {
            ((TextView) view.findViewById(R.id.section_title)).setText(row.title);
            return;
        }
        ((TextView) view.findViewById(R.id.row_title)).setText(row.title);
        ContentUi.textOrGone((TextView) view.findViewById(R.id.row_subtitle), row.subtitle);
        ImageView picture = view.findViewById(R.id.row_picture);
        TextView letter = view.findViewById(R.id.row_letter);
        if (TextUtils.isEmpty(row.imageUrl)) {
            picture.setImageDrawable(null);
            letter.setVisibility(View.VISIBLE);
            letter.setText(TextUtils.isEmpty(row.title) ? "" : row.title.substring(0, row.title.offsetByCodePoints(0, 1)).toUpperCase());
        } else {
            letter.setVisibility(View.GONE);
            ContentUi.image(view.getContext(), row.imageUrl, picture, R.drawable.bg_cc_banner);
        }
        ContentUi.tint((ImageView) view.findViewById(R.id.row_chevron), R.drawable.ic_cc_chevron, R.color.cc_muted);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRow(row);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }
}
