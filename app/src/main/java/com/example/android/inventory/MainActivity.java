package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventory.data.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //loader ID for our product loader
    private static final int PRODUCT_LOADER = 0;

    //our product cursorAdapter
    private ProductCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ItemEditActivity.class);
                startActivity(intent);
            }
        });

        //find the listview to display our products
        ListView productListView = (ListView) findViewById(R.id.list);

        //find and set empty view on the ListView, so that it only shows when the list has 0 items
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        //setup our adapter and attach it to our ListView
        cursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(cursorAdapter);

        //setup an item click listener on our product list
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //create a new intent to open the ItemEditActivity
                Intent intent = new Intent(MainActivity.this, ItemEditActivity.class);

                //set the URI of the clicked product on the intent's data field
                intent.setData(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id));

                //launch the ItemEditActivity so that we can display the current product's data
                startActivity(intent);
            }
        });

        //start the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        //set up our projection to specify the table columns we want
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY};

        //create a loader to execute the ContentProvider query on a background thread
        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //update the product CursorAdapter with the queried product data
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //delete our cursor data
        cursorAdapter.swapCursor(null);
    }
}
