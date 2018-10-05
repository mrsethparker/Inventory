package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.ProductContract.ProductEntry;

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //database doesn't exist so let's create it
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_INVENTORY_TABLE =
                "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                        ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                        ProductEntry.COLUMN_PRICE + " REAL NOT NULL," +
                        ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL," +
                        ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                        ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
