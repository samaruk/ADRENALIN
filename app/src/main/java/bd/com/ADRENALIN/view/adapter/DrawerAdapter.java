package bd.com.ADRENALIN.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import bd.com.ADRENALIN.R;
import bd.com.ADRENALIN.pojo.DrawerItem;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    private Context mContext;
    private ArrayList<DrawerItem> drawerItems;

    public DrawerAdapter(Context mContext, int layoutResourceId, ArrayList<DrawerItem> drawerItems) {
        super(mContext, layoutResourceId, drawerItems);

        this.mContext = mContext;
        this.drawerItems = drawerItems;
    }

    protected class DrawerViewHolder {
        TextView tvTitle;
        ImageView imgIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        DrawerViewHolder drawerViewHolder = new DrawerViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_drawer, viewGroup, false);

            drawerViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.txt_drawer_item_title);
            drawerViewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.img_drawer_item_image);

            convertView.setTag(drawerViewHolder);
        } else {
            drawerViewHolder = (DrawerViewHolder) convertView.getTag();
        }

        DrawerItem drawerItem = drawerItems.get(position);

        drawerViewHolder.tvTitle.setText(drawerItem.getName());
//        LOG.e("icon", drawerItem.getImgIconId()+"");
        if (drawerItem.getImgIconId() != 0)
            drawerViewHolder.imgIcon.setImageResource(drawerItem.getImgIconId());

        return convertView;
    }

}
