package com.tanmay.orderfoodhere.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tanmay.orderfoodhere.R;
import com.tanmay.orderfoodhere.view.interfaces.OnDrawerItemClickListener;

/**
 * Created by TaNMay on 5/31/2016.
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public static OnDrawerItemClickListener click;

    Context context;
    int[] icons;
    private String[] titles;

    public DrawerAdapter(Context context, String titles[], int[] icons) {
        this.titles = titles;
        this.context = context;
        this.icons = icons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType);
            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
            ViewHolder vhHeader = new ViewHolder(v, viewType);
            return vhHeader;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder.Holderid == 1) {
            holder.itemTitle.setText(titles[position - 1]);
            holder.itemIcon.setImageDrawable(context.getResources().getDrawable(icons[position - 1]));
            holder.drawerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.onDrawerItemClick(position);
                }
            });
        } else {
            holder.closeDrawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.onDrawerCloseClick();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return titles.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;
        RelativeLayout drawerItem;
        TextView itemTitle;
        ImageView itemIcon, closeDrawer;

        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == TYPE_ITEM) {
                drawerItem = (RelativeLayout) itemView.findViewById(R.id.di_drawer_item);
                itemTitle = (TextView) itemView.findViewById(R.id.di_option);
                itemIcon = (ImageView) itemView.findViewById(R.id.di_icon);
                Holderid = 1;
            } else {
                closeDrawer = (ImageView) itemView.findViewById(R.id.dh_close);
                Holderid = 0;
            }
        }
    }
}