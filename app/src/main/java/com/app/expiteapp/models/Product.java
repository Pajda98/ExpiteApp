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

    public Long Id = 0l;
    public String Name;
    public String EAN13;

    public String ThumbnailSource;

    public Product(){

    }

    public long insert(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put(ExpiryContract.ProductEntry.NAME, Name);
        values.put(ExpiryContract.ProductEntry.THUMBNAIL, ThumbnailSource);
        values.put(ExpiryContract.ProductEntry.EAN13, EAN13);

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
                ExpiryContract.ProductEntry.EAN13,
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
            product.EAN13 = cursor.getString(2);
            product.ThumbnailSource = cursor.getString(3);
            result.add(product);
        }
        cursor.close();

        return result;
    }

    public static Product search(SQLiteDatabase db, String column, String value)
    {
        String[] projection = {
                BaseColumns._ID,
                ExpiryContract.ProductEntry.NAME,
                ExpiryContract.ProductEntry.EAN13,
                ExpiryContract.ProductEntry.THUMBNAIL
        };

        String selection = column + " = ?";
        String[] selectionArgs = { value };

        Cursor cursor = db.query(
                ExpiryContract.ProductEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Product result = null;

        while(cursor.moveToNext()) {
            result = new Product();
            result.Id = cursor.getLong(0);
            result.Name = cursor.getString(1);
            result.EAN13 = cursor.getString(2);
            result.ThumbnailSource = cursor.getString(3);
        }
        cursor.close();

        return result;
    }
    public static Product get(long id,SQLiteDatabase db){
        return  search(db, BaseColumns._ID, Long.toString(id));
    }
    public int update(SQLiteDatabase db){

        ContentValues values = new ContentValues();
        values.put(ExpiryContract.ProductEntry.NAME, Name);
        values.put(ExpiryContract.ProductEntry.THUMBNAIL, ThumbnailSource);
        values.put(ExpiryContract.ProductEntry.EAN13, EAN13);

        String selection = ExpiryContract.ProductEntry._ID + " = ?";
        String[] selectionArgs = { Long.toString(Id) };


        return db.update(
                ExpiryContract.ProductEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }
}
