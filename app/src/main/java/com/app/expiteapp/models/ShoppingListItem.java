package com.app.expiteapp.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.app.expiteapp.database.ExpiryContract;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListItem {
    public Long Id;
    public String Name;
    public int Done;

    public long insert(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put(ExpiryContract.ShoppingListItem.NAME, Name);
        values.put(ExpiryContract.ShoppingListItem.DONE, Done);

        return db.insert(ExpiryContract.ShoppingListItem.TABLE_NAME, null, values);

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
        return db.delete(ExpiryContract.ShoppingListItem.TABLE_NAME, selection, selectionArgs);
    }

    public static List<ShoppingListItem> getList(SQLiteDatabase db)
    {
        String[] projection = {
                BaseColumns._ID,
                ExpiryContract.ShoppingListItem.NAME,
                ExpiryContract.ShoppingListItem.DONE
        };

        Cursor cursor = db.query(
                ExpiryContract.ShoppingListItem.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<ShoppingListItem> result = new ArrayList<ShoppingListItem>();

        while(cursor.moveToNext()) {
            ShoppingListItem item = new ShoppingListItem();
            item.Id = cursor.getLong(0);
            item.Name = cursor.getString(1);
            item.Done = cursor.getInt(2);
            result.add(item);
        }
        cursor.close();

        return result;
    }

    public int update(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put(ExpiryContract.ShoppingListItem.NAME, Name);
        values.put(ExpiryContract.ShoppingListItem.DONE, Done);

        String selection = ExpiryContract.ShoppingListItem._ID + " = ?";
        String[] selectionArgs = { Long.toString(Id) };

        return db.update(
                ExpiryContract.ShoppingListItem.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

}
