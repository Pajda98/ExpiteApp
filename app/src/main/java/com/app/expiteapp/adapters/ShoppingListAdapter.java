package com.app.expiteapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.expiteapp.MainActivity;
import com.app.expiteapp.R;
import com.app.expiteapp.models.LVPItem;
import com.app.expiteapp.models.ShoppingListItem;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;
import java.util.Random;

public class ShoppingListAdapter extends BaseAdapter {

    private Context context;
    private List<ShoppingListItem> data;
    private LayoutInflater inflater;

    public ShoppingListAdapter(@NonNull Context context, List<ShoppingListItem> objects) {
        this.context = context;
        this.data = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        final ShoppingListItem item = data.get(position);
        convertView = inflater.inflate(R.layout.shopping_list_item, parent, false);
        ShoppingListItemHolder holder = new ShoppingListItemHolder();
        holder.Name = (TextView)convertView.findViewById(R.id.shopping_list_item_name);
        holder.Done = (ImageView) convertView.findViewById(R.id.shopping_list_done);
        holder.Layout = (RelativeLayout)convertView.findViewById(R.id.shopping_list_layout);

        holder.ItemLayout = (SwipeLayout) convertView.findViewById(R.id.shopping_list_item_layout);
        holder.ItemLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.ItemLayout.addDrag(SwipeLayout.DragEdge.Left, convertView.findViewById(R.id.shopping_list_done_swipe));
        convertView.setTag(holder);

        holder.Name.setText(item.Name);
        if(item.Done == 0 ) {
            holder.Done.setVisibility(View.INVISIBLE);
            holder.Layout.setBackgroundResource(R.color.ProductItemColor);
        }else{
            holder.Done.setVisibility(View.VISIBLE);
            holder.Layout.setBackgroundResource(R.color.GreenSuceedColor);
        }

        holder.ItemLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                SwipeLayout.DragEdge dragEdge = holder.ItemLayout.getDragEdge();

                boolean isLeftToRightSwipe = SwipeLayout.DragEdge.Left.equals(dragEdge);
                if(isLeftToRightSwipe) {
                    holder.Done.setVisibility(View.VISIBLE);
                    holder.Layout.setBackgroundResource(R.color.GreenSuceedColor);
                    item.Done = 1;
                    item.update(MainActivity.DB_HELPER.getWritableDatabase());
                } else {
                    data.remove(item);
                    item.delete(MainActivity.DB_HELPER.getWritableDatabase());
                    notifyDataSetChanged();
                }
                holder.ItemLayout.close(true);
            }

            @Override
            public void onClose(SwipeLayout layout) {
            }
        });
        return convertView;
    }


    static class ShoppingListItemHolder
    {
        TextView Name;
        ImageView Done;
        RelativeLayout Layout;
        SwipeLayout ItemLayout;
    }
}
