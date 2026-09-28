package bd.com.ADRENALIN.view.adapter.content;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.util.ContentUi;
import bd.com.ADRENALIN.view.activity.PhotoViewerActivity;

/** A horizontal strip of photos; a tap opens them full screen. */
public class PhotoStripAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<String> urls;

    public PhotoStripAdapter(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cc_photo, parent, false)) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ContentUi.image(context, urls.get(position), (ImageView) holder.itemView.findViewById(R.id.photo_image), R.color.cc_line);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoViewerActivity.class);
                intent.putStringArrayListExtra(PhotoViewerActivity.EXTRA_URLS, new ArrayList<>(urls));
                intent.putExtra(PhotoViewerActivity.EXTRA_INDEX, position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }
}
