package com.app.expiteapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.expiteapp.MainActivity;
import com.app.expiteapp.R;
import com.app.expiteapp.models.Product;

import java.net.URI;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    Context context;
    int layoutResourceId;
    List<Product> data = null;

    public ProductAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProductHolder holder = null;
        final int actualPosition = position;
        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ProductHolder();
            holder.Name = (TextView)row.findViewById(R.id.product_name);
            holder.EAN13 = (TextView)row.findViewById(R.id.product_ean);
            holder.Date = (TextView)row.findViewById(R.id.product_date);
            holder.Image = (ImageView)row.findViewById(R.id.product_icon);
            holder.Delete = (ImageView)row.findViewById(R.id.delete_icon);

            row.setTag(holder);
        }
        else
        {
            holder = (ProductHolder)row.getTag();
        }

        final Product product = data.get(position);
        holder.Name.setText(product.Name);
        holder.EAN13.setText(product.EAN13);
        holder.Date.setText(product.ExpiryDate);

        if (!product.ThumbnailSource.equals("")) {
            holder.Image.setImageURI(Uri.parse(product.ThumbnailSource));
        }else{
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.Image.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            holder.Image.setLayoutParams(params);
        }

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_view_click));
                product.delete(MainActivity.DB_HELPER.getWritableDatabase());

            }
        });

        return row;
    }

    static class ProductHolder
    {
        ImageView Image;
        TextView Name;
        TextView EAN13;
        TextView Date;
        ImageView Delete;
    }

}
