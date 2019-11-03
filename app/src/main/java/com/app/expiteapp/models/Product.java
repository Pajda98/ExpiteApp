package com.app.expiteapp.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.BaseColumns;

import com.app.expiteapp.database.ExpiryContract;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Product {

    public Long Id;
    public String Name;
    public String Description;
    public String EAN13;

    public String ExpiryDate;
    public Date getExpiryDate(){
        try {
            return getMyDateFormat().parse(ExpiryDate);
        }
        catch (ParseException ex){
            return null;
        }
    }
    public void setExpiryDate(Date date){
        ExpiryDate = getMyDateFormat().format(date);
    }

    public String ThumbnailSource;

    public Product(){

    }

    private DateFormat getMyDateFormat(){
       return new SimpleDateFormat("yyyy-mm-dd") ;
    }

    public long insert(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put(ExpiryContract.ProductEntry.NAME, Name);
        values.put(ExpiryContract.ProductEntry.DESCRIPTION, Description);
        values.put(ExpiryContract.ProductEntry.THUMBNAIL, ThumbnailSource);
        values.put(ExpiryContract.ProductEntry.EAN13, EAN13);
        values.put(ExpiryContract.ProductEntry.EXPIRYDATE, ExpiryDate);

       return db.insert(ExpiryContract.ProductEntry.TABLE_NAME, null, values);

    }

    public int delete(SQLiteDatabase db)
    {
        return delete(db, this.Id);
    }

    public static int delete(SQLiteDatabase db, long id)
    {
        ContentValues values = new ContentValues();
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };
        return db.delete(ExpiryContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
    }

    public static List<Product> getList(SQLiteDatabase db)
    {
        String[] projection = {
                BaseColumns._ID,
                ExpiryContract.ProductEntry.NAME,
                ExpiryContract.ProductEntry.DESCRIPTION,
                ExpiryContract.ProductEntry.EAN13,
                ExpiryContract.ProductEntry.EXPIRYDATE,
                ExpiryContract.ProductEntry.THUMBNAIL
        };

        Cursor cursor = db.query(
                ExpiryContract.ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<Product> result = new ArrayList<Product>();

        while(cursor.moveToNext()) {
            Product product = new Product();
            product.Id = cursor.getLong(0);
            product.Name = cursor.getString(1);
            product.Description = cursor.getString(2);
            product.EAN13 = cursor.getString(3);
            product.ExpiryDate = cursor.getString(4);
            product.ThumbnailSource = cursor.getString(5);
            result.add(product);
        }
        cursor.close();

        return result;
    }
}
