package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventory.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public ItemDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //database doesn't exist so let's create it
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_INVENTORY_TABLE =
                "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                        ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ItemEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                        ItemEntry.COLUMN_PRICE + " REAL NOT NULL," +
                        ItemEntry.COLUMN_QUANTITY + " INTEGER NOT NULL," +
                        ItemEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                        ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
