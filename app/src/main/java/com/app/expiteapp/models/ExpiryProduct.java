package com.app.expiteapp.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.app.expiteapp.database.ExpiryContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpiryProduct {

    public Long Id;
    public Long ProductId;

    public String Notes;

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

    public static DateFormat getMyDateFormat(){
        return new SimpleDateFormat("dd.MM.yyyy") ;
    }

    public static ExpiryProduct get(long id, SQLiteDatabase db)
    {
        String[] projection = {
                BaseColumns._ID,
                ExpiryContract.ExiryProductEntry.PRODUCT_ID,
                ExpiryContract.ExiryProductEntry.EXPIRYDATE,
                ExpiryContract.ExiryProductEntry.NOTES
        };

        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = { Long.toString(id) };

        Cursor cursor = db.query(
                ExpiryContract.ExiryProductEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        List<ExpiryProduct> result = new ArrayList<ExpiryProduct>();

        while(cursor.moveToNext()) {
            ExpiryProduct expiryProduct = new ExpiryProduct();
            expiryProduct.Id = cursor.getLong(0);
            expiryProduct.ProductId = cursor.getLong(1);
            expiryProduct.ExpiryDate = cursor.getString(2);
            expiryProduct.Notes = cursor.getString(3);
            result.add(expiryProduct);
        }
        cursor.close();

        if(result.size() > 0) return result.get(0);
        else return null;
    }

    public long insert(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put(ExpiryContract.ExiryProductEntry.PRODUCT_ID, ProductId);
        values.put(ExpiryContract.ExiryProductEntry.EXPIRYDATE, ExpiryDate);
        values.put(ExpiryContract.ExiryProductEntry.NOTES, Notes);

        return db.insert(ExpiryContract.ExiryProductEntry.TABLE_NAME, null, values);

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
        return db.delete(ExpiryContract.ExiryProductEntry.TABLE_NAME, selection, selectionArgs);
    }

    public static List<ExpiryProduct> getList(SQLiteDatabase db)
    {
        String[] projection = {
                BaseColumns._ID,
                ExpiryContract.ExiryProductEntry.PRODUCT_ID,
                ExpiryContract.ExiryProductEntry.EXPIRYDATE,
                ExpiryContract.ExiryProductEntry.NOTES
        };

        Cursor cursor = db.query(
                ExpiryContract.ExiryProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<ExpiryProduct> result = new ArrayList<ExpiryProduct>();

        while(cursor.moveToNext()) {
            ExpiryProduct expiryProduct = new ExpiryProduct();
            expiryProduct.Id = cursor.getLong(0);
            expiryProduct.ProductId = cursor.getLong(1);
            expiryProduct.ExpiryDate = cursor.getString(2);
            expiryProduct.Notes = cursor.getString(3);
            result.add(expiryProduct);
        }
        cursor.close();

        return result;
    }

    public int update(SQLiteDatabase db){

        ContentValues values = new ContentValues();
        values.put(ExpiryContract.ExiryProductEntry.EXPIRYDATE, ExpiryDate);
        values.put(ExpiryContract.ExiryProductEntry.NOTES, Notes);

        String selection = ExpiryContract.ProductEntry._ID + " = ?";
        String[] selectionArgs = { Long.toString(Id) };

        return db.update(
                ExpiryContract.ProductEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }

}
