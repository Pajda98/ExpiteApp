package com.app.expiteapp.models;

import android.content.Context;
import android.content.res.TypedArray;

import com.app.expiteapp.R;

import java.lang.reflect.Array;
import java.text.Format;
import java.util.Arrays;

public class ProductGroupLevels {
    Context context;
    public ProductGroupLevels(Context context)
    {
        this.context = context;
    }

    public Integer[] expiryDelays = new Integer[]{0, 7, 30};

    public String getExpiryText(int delay){
        int pos = Arrays.asList(expiryDelays).indexOf(delay);
        if(pos == 0){
            return this.context.getResources().getString(R.string.delay_none);
        }
        else if (pos > 0 && pos < expiryDelays.length ){
            return String.format(this.context.getResources().getString(R.string.delay_less), delay);
        }
        return String.format(this.context.getResources().getString(R.string.delay_more), expiryDelays[expiryDelays.length - 1]);
    }

    public int getExpiryImageResource(int delay){
        if(delay == -1){
            return R.drawable.ic_donut_large_green_18dp;
        }

        int pos = Arrays.asList(expiryDelays).indexOf(delay);
        TypedArray icons = context.getResources().obtainTypedArray(R.array.images);
        if(pos > -1) {
            return icons.getResourceId(pos, R.drawable.ic_donut_large_red_18dp);
        }
        return  R.drawable.ic_donut_large_red_18dp;
    }
}
