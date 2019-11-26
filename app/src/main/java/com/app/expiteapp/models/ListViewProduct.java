package com.app.expiteapp.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.app.expiteapp.database.ExpiryContract;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListViewProduct implements Comparable<ListViewProduct> {
    public Long ExpiryProductId;
    public String Name;
    public String EAN13;
    public String ThumbnailSource;
    public String ExpiryDate;

    public Date getExpiryDate() {
        try {
            return ExpiryProduct.getMyDateFormat().parse(ExpiryDate);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static List<ListViewProduct> getList(SQLiteDatabase db)
    {
        final String MY_QUERY = "SELECT " +
                "ep." + BaseColumns._ID + ", " +
                ExpiryContract.ExiryProductEntry.EXPIRYDATE + ", " +
                ExpiryContract.ProductEntry.NAME + ", " +
                ExpiryContract.ProductEntry.EAN13 + ", " +
                ExpiryContract.ProductEntry.THUMBNAIL + " FROM " +
                ExpiryContract.ExiryProductEntry.TABLE_NAME  + " ep INNER JOIN " +
                ExpiryContract.ProductEntry.TABLE_NAME + " p ON p." + BaseColumns._ID + "=ep." + ExpiryContract.ExiryProductEntry.PRODUCT_ID;

        Cursor cursor = db.rawQuery(MY_QUERY, null);

        List<ListViewProduct> result = new ArrayList<ListViewProduct>();

        while(cursor.moveToNext()) {
            ListViewProduct p = new ListViewProduct();
            p.ExpiryProductId = cursor.getLong(0);
            p.ExpiryDate = cursor.getString(1);
            p.Name = cursor.getString(2);
            p.EAN13 = cursor.getString(3);
            p.ThumbnailSource = cursor.getString(4);
            result.add(p);
        }
        cursor.close();

        return result;
    }

    @Override
    public int compareTo(ListViewProduct o) {
        return getExpiryDate().compareTo(o.getExpiryDate());
    }
}
