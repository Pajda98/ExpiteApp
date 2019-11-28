package com.app.expiteapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpiryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "ExpiryApp.db";

    public ExpiryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ExpiryContract.SQL_CREATE_PRODUCTS);
        db.execSQL(ExpiryContract.SQL_CREATE_EXPIRY_PRODUCTS);
        db.execSQL(ExpiryContract.SQL_CREATE_SHOPPING_LIST_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ExpiryContract.SQL_DELETE_PRODUCTS);
        db.execSQL(ExpiryContract.SQL_DELETE_EXPIRY_PRODUCTS);
        db.execSQL(ExpiryContract.SQL_DELETE_SHOPPING_LIST_ITEM);
        onCreate(db);
    }

}
