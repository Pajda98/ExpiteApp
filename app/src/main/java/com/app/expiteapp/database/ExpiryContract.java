package com.app.expiteapp.database;

import android.provider.BaseColumns;

import java.util.Date;

public final class ExpiryContract {
    private ExpiryContract() {}

    public static final String SQL_CREATE_PRODUCTS =
            "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                    ProductEntry._ID + " INTEGER PRIMARY KEY," +
                    ProductEntry.NAME + " TEXT," +
                    ProductEntry.THUMBNAIL + " TEXT," +
                    ProductEntry.EAN13 + " TEXT NOT NULL UNIQUE)";

    public static final String SQL_DELETE_PRODUCTS =
            "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "product";
        public static final String NAME = "name";
        public static final String THUMBNAIL = "thumbnail";
        public static final String EAN13 = "ean13";;
    }

    public static final String SQL_CREATE_EXPIRY_PRODUCTS =
            "CREATE TABLE " + ExiryProductEntry.TABLE_NAME + " (" +
                    ExiryProductEntry._ID + " INTEGER PRIMARY KEY," +
                    ExiryProductEntry.PRODUCT_ID + " INTEGER NOT NULL," +
                    ExiryProductEntry.NOTES + " TEXT," +
                    ExiryProductEntry.EXPIRYDATE + " DATE, " +
                    "FOREIGN KEY (product_id) REFERENCES product(_id))";

    public static final String SQL_DELETE_EXPIRY_PRODUCTS =
            "DROP TABLE IF EXISTS " + ExiryProductEntry.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static class ExiryProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "expiry_product";
        public static final String PRODUCT_ID = "product_id";
        public static final String NOTES = "notes";
        public static final String EXPIRYDATE = "expiry_date";
    }

    public static final String SQL_CREATE_SHOPPING_LIST_ITEM =
            "CREATE TABLE " + ShoppingListItem.TABLE_NAME + " (" +
                    ShoppingListItem._ID + " INTEGER PRIMARY KEY," +
                    ShoppingListItem.NAME + " TEXT NOT NULL," +
                    ShoppingListItem.DONE + " INTEGER)";

    public static final String SQL_DELETE_SHOPPING_LIST_ITEM =
            "DROP TABLE IF EXISTS " + ShoppingListItem.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static class ShoppingListItem implements BaseColumns {
        public static final String TABLE_NAME = "shopping_list_item";
        public static final String NAME = "name";
        public static final String DONE = "done";
    }

}
