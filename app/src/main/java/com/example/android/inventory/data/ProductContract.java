package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {

    //our content authority
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";
    //our base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //the path for our tables
    public static final String PATH_PRODUCTS = "products";

    //private constructor since we don't want to accidentally instantiate the contract class
    private ProductContract() {
    }

    //define the structure of our item table
    public static class ProductEntry implements BaseColumns {

        //MIME type for a list of products
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //MIME type for a single product
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //our complete content URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

    }
}
