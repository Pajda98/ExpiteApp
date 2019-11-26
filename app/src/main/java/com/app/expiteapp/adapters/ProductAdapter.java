package com.app.expiteapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.net.URI;
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
            convertView = inflater.inflate(R.layout.item_product_layout, parent, false);
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
        holder.Delete = (ImageView)row.findViewById(R.id.delete_icon);
        return holder;
    }

    private void fillHolder(ProductHolder holder, final LVPItem product){
        holder.Name.setText(product.productData.Name);
        holder.EAN13.setText((product.productData.EAN13.contains("EAN"))? "" : product.productData.EAN13);
        holder.Date.setText(product.productData.ExpiryDate);

        if (!product.productData.ThumbnailSource.equals("")) {
            //if(holder.Image.getDrawable() instanceof BitmapDrawable) ((BitmapDrawable)holder.Image.getDrawable()).getBitmap().recycle();
            holder.Image.setImageURI(Uri.parse(product.productData.ThumbnailSource));
        }else{
//            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//            holder.Image.setColorFilter(color);
        }

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_view_click));
                ExpiryProduct.delete(MainActivity.DB_HELPER.getWritableDatabase(), product.productData.ExpiryProductId);

            }
        });
    }


    static class ProductHolder
    {
        ImageView Image;
        TextView Name;
        TextView EAN13;
        TextView Date;
        ImageView Delete;
    }

    static class HeaderHolder
    {
        ImageView Image;
        TextView Time;
    }

}
