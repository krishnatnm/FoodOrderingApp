package com.tanmay.orderfoodhere.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tanmay.orderfoodhere.R;
import com.tanmay.orderfoodhere.view.interfaces.OnListItemClickListener;
import com.tanmay.orderfoodhere.view.models.FoodItem;

import java.util.ArrayList;

/**
 * Created by TaNMay on 6/1/2016.
 */
public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public static OnListItemClickListener click;
    Context context;
    ArrayList<FoodItem> foodItems;

    public FoodItemAdapter(Context context, ArrayList<FoodItem> foodItems) {
        super();
        this.context = context;
        this.foodItems = foodItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
            ViewHolder vhItem = new ViewHolder(v, viewType);
            return vhItem;
        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_header, parent, false);
            ViewHolder vhHeader = new ViewHolder(v, viewType);
            return vhHeader;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder.Holderid == 1) {
            viewHolder.name.setText(foodItems.get(position - 1).getName());
            viewHolder.cost.setText(foodItems.get(position - 1).getCost());
            viewHolder.likes.setText(" " + foodItems.get(position - 1).getLikes());
            viewHolder.item.setBackground(context.getResources().getDrawable(foodItems.get(position - 1).getImage()));
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return foodItems.size() + 1;
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
        public RelativeLayout item;
        public TextView likes, cost, name;
        int Holderid;

        public ViewHolder(View itemView, int ViewType) {
            super(itemView);
            if (ViewType == TYPE_ITEM) {
                item = (RelativeLayout) itemView.findViewById(R.id.fi_item);
                likes = (TextView) itemView.findViewById(R.id.fi_likes);
                cost = (TextView) itemView.findViewById(R.id.fi_amount);
                name = (TextView) itemView.findViewById(R.id.fi_item_name);
                Holderid = 1;
            } else {
                Holderid = 0;
            }
        }
    }
}