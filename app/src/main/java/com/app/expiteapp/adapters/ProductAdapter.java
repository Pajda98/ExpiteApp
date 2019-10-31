package com.app.expiteapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.expiteapp.R;
import com.app.expiteapp.models.Product;

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

        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ProductHolder();
            holder.txt = (TextView)row.findViewById(R.id.ProductName);


            row.setTag(holder);
        }
        else
        {
            holder = (ProductHolder)row.getTag();
        }

        Product product = data.get(position);
        holder.txt.setText(product.Name);

        return row;
    }

    static class ProductHolder
    {
        TextView txt;
    }

}
