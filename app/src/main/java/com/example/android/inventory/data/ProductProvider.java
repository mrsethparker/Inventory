package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventory.data.ProductContract.ProductEntry;

//ContentProvider for our Inventory app
public class ProductProvider extends ContentProvider {

    //tag for our log messages
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    //URI matcher code for our products table content URI
    private static final int PRODUCTS = 100;
    //URI matcher code for the content URI of a single product in our products table
    private static final int PRODUCT_ID = 101;
    //create a URI matcher so that we can match a content URI to it's code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //set up our static initialize that will run the first time our provider is called
    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    //database helper object
    private ProductDbHelper mDbHelper;

    //initialize our provider and our database helper object
    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    //perform our query for the given URI. Use the given projection, selection, selection arguments, and sort order.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        //grab a readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //cursor to hold the result of our query
        Cursor cursor;

        //see if the URI matcher can match our URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                //directly query our database with the given arguments
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCT_ID:
                //extract the ID from the URI then query our database looking for that ID
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("We can't query with the unknown URI " + uri);
        }

        return cursor;
    }

    //insert our new data into the provider with the given ContentValues
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion isn't supported for " + uri);
        }
    }

    //updates our data at the given selection and selection arguments, with the new ContentValues
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update isn't supported for " + uri);
        }

    }

    //delete our data at the given selection and selection arguments
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        //get a writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                //delete all rows that match the selection and selection args
                return database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
            case PRODUCT_ID:
                //delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion isn't supported for " + uri);
        }
    }

    //returns the MIME type of data for our content URI
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        //check that the product name isn't null
        String productName = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Products require a name");
        }

        //check that the product price isn't null
        Double price = values.getAsDouble(ProductEntry.COLUMN_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Products require a price");
        }

        //check that the product quantity isn't null
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Products require a quantity");
        }

        //check that the product supplier isn't null
        String supplierName = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Products require a supplier name");
        }

        //check that the product supplier phone isn't null
        String supplierPhone = values.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Products require a supplier phone");
        }


        //get our data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //insert the new row
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //now we know the ID of the new row so return the new URI with the ID appended
        return ContentUris.withAppendedId(uri, newRowId);
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Products require a name");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRICE)) {
            Double price = values.getAsDouble(ProductEntry.COLUMN_PRICE);
            ;
            if (price == null) {
                throw new IllegalArgumentException("Products require a price");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Products require a quantity");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Products require a supplier name");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhone = values.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Products require a supplier phone");
            }
        }

        //get our database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //update the db and return the number of rows that were affected by our update
        return db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}