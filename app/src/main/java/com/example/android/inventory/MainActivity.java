package com.example.android.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;
import com.example.android.inventory.data.ItemContract.ItemEntry;

import com.example.android.inventory.data.ItemDbHelper;

public class MainActivity extends AppCompatActivity {

    private ItemDbHelper dbHelper;

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

        dbHelper = new ItemDbHelper(this);
        readDb();

    }

    @Override
    protected void onStart() {
        super.onStart();
        readDb();
    }

    private void readDb() {

        //create and/or open our database so that we can read from it
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //set up our SQL query
        String[] projection = {
                BaseColumns._ID,
                ItemEntry.COLUMN_PRODUCT_NAME,
                ItemEntry.COLUMN_PRICE,
                ItemEntry.COLUMN_QUANTITY,
                ItemEntry.COLUMN_SUPPLIER_NAME,
                ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        TextView displayView = (TextView) findViewById(R.id.text_view_test);

        //query our database
        //NOTE: no need to manually close our cursor when we're done since we're making use of automatic resource management
        try (Cursor cursor = db.query(
                ItemEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null)) {
            //display our database rows
            displayView.setText(R.string.rows_header_text);
            displayView.append(cursor.getCount() + "\n\n" +
                    ItemEntry._ID + " | " +
                    ItemEntry.COLUMN_PRODUCT_NAME + " | " +
                    ItemEntry.COLUMN_PRICE + " | " +
                    ItemEntry.COLUMN_QUANTITY + " | " +
                    ItemEntry.COLUMN_SUPPLIER_NAME + " | " +
                    ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            //figure out the column indices of our table
            int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            //loop through all the rows in our cursor
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                double currentPrice = cursor.getDouble(priceColumnIndex);
                int currentQty = cursor.getInt(qtyColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                //display the values from each column of the current row in our cursor in our TextView
                displayView.append("\n" +
                        currentID + "-" +
                        currentName + "-" +
                        currentPrice + "-" +
                        currentQty + "-" +
                        currentSupplierName + "-" +
                        currentSupplierPhone);
            }
        }
    }
}
