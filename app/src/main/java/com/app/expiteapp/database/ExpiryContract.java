package com.app.expiteapp.database;

import android.provider.BaseColumns;

import java.util.Date;

public final class ExpiryContract {
    private ExpiryContract() {}

    public static final String SQL_CREATE_PRODUCTS =
            "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                    ProductEntry._ID + " INTEGER PRIMARY KEY," +
                    ProductEntry.NAME + " TEXT," +
                    ProductEntry.DESCRIPTION + " TEXT," +
                    ProductEntry.THUMBNAIL + " TEXT," +
                    ProductEntry.EAN13 + " TEXT," +
                    ProductEntry.EXPIRYDATE + " DATE)";

    public static final String SQL_DELETE_PRODUCTS =
            "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "product";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String THUMBNAIL = "thumbnail";
        public static final String EAN13 = "ean13";
        public static final String EXPIRYDATE = "expiry_date";


    }

}
