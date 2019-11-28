package com.app.expiteapp.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LVPItem {
    public Long id;
    public String title;
    public int delayGroup;
    public boolean isHeader;
    public ListViewProduct productData;
    public boolean simpleClickShow = false;

    public static List<LVPItem> generateFormProducts(List<ListViewProduct> products, Context context){
        ProductGroupLevels levels = new ProductGroupLevels(context);
        List<LVPItem> fullProductList = new ArrayList<LVPItem>();
        int levelsInList = 0;
        for(int delay : levels.expiryDelays){
            int position = -1;
            for (final ListViewProduct product: products) {
                if(ProductGroupLevels.InDelayCategory(product, delay)) {
                    if(ContainsProduct(fullProductList, product.ExpiryProductId)) continue;
                    if(position == -1) position = fullProductList.size();
                    fullProductList.add(createFromProduct(product));
                }
            }
            if(position != -1){
                levelsInList++;
                fullProductList.add(position,createFromLevel(levels, delay));
            }
        }

        if (fullProductList.size()-levelsInList != products.size()) {
            fullProductList.add(createFromLevel(levels, -1));
            for (final ListViewProduct product: products) {
                if(ContainsProduct(fullProductList, product.ExpiryProductId)) continue;
                fullProductList.add(createFromProduct(product));
            }
        }
        return fullProductList;
    }
    private static LVPItem createFromLevel(ProductGroupLevels levels, int delay){
        LVPItem newItem = new LVPItem();
        newItem.id = -1l;
        newItem.delayGroup = delay;
        newItem.isHeader = true;
        newItem.title = levels.getExpiryText(delay);
        return newItem;
    }
    private static LVPItem createFromProduct(ListViewProduct product){
        LVPItem newProductItem = new LVPItem();
        newProductItem.id = product.ExpiryProductId;
        newProductItem.isHeader = false;
        newProductItem.title = product.Name;
        newProductItem.productData = product;
        return newProductItem;
    }
    private static boolean ContainsProduct(List<LVPItem> fullProductList, long id){
        for (LVPItem item: fullProductList) {
            if(item.id == id) return true;
        }
        return false;
    }

}
