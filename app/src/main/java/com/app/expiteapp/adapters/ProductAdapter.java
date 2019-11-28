package com.app.expiteapp.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.FitWindowsLinearLayout;
import androidx.core.content.ContextCompat;

import com.app.expiteapp.MainActivity;
import com.app.expiteapp.R;
import com.app.expiteapp.database.ExpiryContract;
import com.app.expiteapp.models.ExpiryProduct;
import com.app.expiteapp.models.LVPItem;
import com.app.expiteapp.models.ListViewProduct;
import com.app.expiteapp.models.ProductGroupLevels;
import com.app.expiteapp.models.ShoppingListItem;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ProductAdapter extends BaseAdapter {

    Context context;
    int layoutResourceId;
    List<LVPItem> data = null;
    LayoutInflater inflater;
    Random rnd;

    public ProductAdapter(@NonNull Context context, List<LVPItem> objects) {
        this.context = context;
        this.data = objects;
        this.rnd = new Random();
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
        //final int actualPosition = position;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        final LVPItem product = data.get(position);

        if(product.isHeader){
            convertView = inflater.inflate(R.layout.group_product_layout, parent, false);
            HeaderHolder holder = new HeaderHolder();

            holder.Time = convertView.findViewById(R.id.time_value);
            holder.Image = convertView.findViewById(R.id.time_image);
            convertView.setTag(holder);

            holder.Time.setText(product.title);
            ProductGroupLevels levels = new ProductGroupLevels(context);
            //if(holder.Image.getDrawable() instanceof BitmapDrawable) ((BitmapDrawable)holder.Image.getDrawable()).getBitmap().recycle();
            holder.Image.setImageResource(levels.getExpiryImageResource(product.delayGroup));
        }
        else{
            convertView = inflater.inflate(R.layout.swipable_product_layout, parent, false);
            ProductHolder holder = initHolder(convertView);
            convertView.setTag(holder);

            fillHolder(holder, product);
        }


        return convertView;
    }

    private ProductHolder initHolder(View row){
        ProductHolder holder = new ProductHolder();
        holder.Name = (TextView)row.findViewById(R.id.product_name);
        holder.EAN13 = (TextView)row.findViewById(R.id.product_ean);
        holder.Date = (TextView)row.findViewById(R.id.product_date);
        holder.Image = (ImageView)row.findViewById(R.id.product_icon);

        holder.AddToList = (LinearLayout) row.findViewById(R.id.add_to_list);
//        holder.EditProduct = (LinearLayout)row.findViewById(R.id.edit_product);
        holder.DeleteProduct = (LinearLayout)row.findViewById(R.id.delete_product);

        holder.productLayout = (SwipeLayout)row.findViewById(R.id.swipable_poduct_item);
        holder.productLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        //holder.productLayout.addDrag(SwipeLayout.DragEdge.Left, row.findViewById(R.id.swipableItemContext));
        return holder;
    }

    private void fillHolder(ProductHolder holder, final LVPItem product){
        holder.Name.setText(product.productData.Name);
        holder.EAN13.setText((product.productData.EAN13.contains("EAN"))? "" : product.productData.EAN13);
        holder.Date.setText(product.productData.ExpiryDate);

        if (!product.productData.ThumbnailSource.equals("")) {
            //if(holder.Image.getDrawable() instanceof BitmapDrawable) ((BitmapDrawable)holder.Image.getDrawable()).getBitmap().recycle();

            //final int THUMBSIZE = 128;
            try {
                File file = new File(new URI(product.productData.ThumbnailSource));
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(file.getAbsolutePath()),
                        512 ,
                        512);
                holder.Image.setImageBitmap(thumbImage);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
//            holder.Image.setImageBitmap(Uri.parse(product.productData.ThumbnailSource));
        }else{
//            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//            holder.Image.setColorFilter(color);
        }

        holder.productLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                product.productData.isSwipeLayoutOpen = true;
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                if(product.simpleClickShow && leftOffset <= -10){
                    holder.productLayout.close(true);
                    product.simpleClickShow = false;
                }
            }

            @Override
            public void onClose(SwipeLayout layout) {
                product.productData.isSwipeLayoutOpen = false;

            }
        });
        holder.productLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (product.productData.isSwipeLayoutOpen) holder.productLayout.open(false);
            }
        });

        holder.AddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.productLayout.close(true);
                ShoppingListItem sli = new ShoppingListItem();
                sli.Name = product.productData.Name;
                sli.Done = 0;
                sli.insert(MainActivity.DB_HELPER.getWritableDatabase());
            }
        });
//        holder.EditProduct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        holder.DeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.productLayout.close(true);
//                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_view_click));
                data.remove(product);
                ExpiryProduct.delete(MainActivity.DB_HELPER.getWritableDatabase(), product.productData.ExpiryProductId);
                //Delete headers
                List<LVPItem> deleteList = new ArrayList<LVPItem>();
                for(int i = 0; i < data.size() - 1; i++){
                    if(data.get(i).isHeader && data.get(i+1).isHeader){
                        deleteList.add(data.get(i));
                    }
                }
                for(LVPItem item : deleteList){
                    data.remove(item);
                }
                notifyDataSetChanged();
            }
        });
    }


    static class ProductHolder
    {
        ImageView Image;
        TextView Name;
        TextView EAN13;
        TextView Date;
        LinearLayout AddToList;
//        LinearLayout EditProduct;
        LinearLayout DeleteProduct;
        SwipeLayout productLayout;
    }

    static class HeaderHolder
    {
        ImageView Image;
        TextView Time;
    }

}
