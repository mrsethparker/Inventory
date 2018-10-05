package com.example.android.inventory;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract.ProductEntry;
import com.example.android.inventory.data.ProductDbHelper;

public class ItemEditActivity extends AppCompatActivity {

    //EditText field for our product's name
    private EditText productName;

    //EditText field for our product's price
    private EditText productPrice;

    //EditText field for our product's quantity
    private EditText productQuantity;

    //EditText field for our product's supplier name
    private EditText supplierName;

    //EditText field for our product's supplier phone
    private EditText supplierPhone;

    //our database helper
    private ProductDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        //find all the views that we'll read user input from
        productName = (EditText) findViewById(R.id.item);
        productPrice = (EditText) findViewById(R.id.price);
        productQuantity = (EditText) findViewById(R.id.quantity);
        supplierName = (EditText) findViewById(R.id.supplier_name);
        supplierPhone = (EditText) findViewById(R.id.supplier_phone);

    }

    private void insertItem(){

        //create a new map of our values
        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName.getText().toString().trim());
        values.put(ProductEntry.COLUMN_PRICE, Double.parseDouble(productPrice.getText().toString().trim()));
        values.put(ProductEntry.COLUMN_QUANTITY, Integer.parseInt(productQuantity.getText().toString().trim()));
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName.getText().toString().trim());
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone.getText().toString().trim());

        //insert our new row
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        //display a toast indicating if our insert was successful or not
        if (newUri == null) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.productSaveError, Toast.LENGTH_SHORT);
            toast.show();
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), R.string.productSaved, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.save_action:
                insertItem();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.delete_action:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
