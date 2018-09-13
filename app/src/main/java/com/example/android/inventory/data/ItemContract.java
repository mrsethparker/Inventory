package com.example.android.inventory.data;

import android.provider.BaseColumns;

public final class ItemContract {

    //private constructor since we don't want to accidentally instantiate the contract class
    private ItemContract (){}

    //define the structure of our item table
    public static class ItemEntry implements BaseColumns {

        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

    }
}
