package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract.ProductEntry;

//allows a user to create a new product or edit an existing product
public class ItemEditActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //identifier for our product data loader
    private static final int EXISTING_PRODUCT_LOADER = 0;

    //current URI for a product
    private Uri currentProductUri;

    //EditText field for our product's name
    private EditText productNameEditText;

    //EditText field for our product's price
    private EditText productPriceEditText;

    //EditText field for our product's quantity
    private EditText productQuantityEditText;

    //EditText field for our product's supplier name
    private EditText supplierNameEditText;

    //EditText field for our product's supplier phone
    private EditText supplierPhoneEditText;

    //flag to keep track of whether or not a product has been edited
    private boolean productHasChanged = false;

    //OnTouchListener to monitor for whether or not a user has edited anythign
    private View.OnTouchListener productTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        //look at the data of the intent used to launch this activity
        //so we can figure out if we're creating a new product or editing
        //an existing product
        Intent intent = getIntent();
        currentProductUri = intent.getData();

        //if the intent doesn't include a URI, then we know that we're creating a
        //new product
        if (currentProductUri == null) {
            //new product is being added so let's change the app bar to reflect that
            setTitle("Add a Product");
            //need to change the available menu options if we're adding a new product
            invalidateOptionsMenu();
        } else {
            //existing product is being edited so let's change the app bar to reflect that
            setTitle("Edit Product");

            //initialize a loader to pull the current product's data from our database
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        //find all the views that we'll read user input from
        productNameEditText = (EditText) findViewById(R.id.item);
        productPriceEditText = (EditText) findViewById(R.id.price);
        productQuantityEditText = (EditText) findViewById(R.id.quantity);
        supplierNameEditText = (EditText) findViewById(R.id.supplier_name);
        supplierPhoneEditText = (EditText) findViewById(R.id.supplier_phone);

        //set OnTouchListeners on all of our input fields so we can track if any of them
        //have been changed or not
        productNameEditText.setOnTouchListener(productTouchListener);
        productPriceEditText.setOnTouchListener(productTouchListener);
        productQuantityEditText.setOnTouchListener(productTouchListener);
        supplierNameEditText.setOnTouchListener(productTouchListener);
        supplierPhoneEditText.setOnTouchListener(productTouchListener);

        //only show the order button on the existing products
        if (currentProductUri != null) {
            //find the order button and make it visible
            final Button orderButton = (Button) findViewById(R.id.order_button);
            orderButton.setVisibility(View.VISIBLE);

            //set a click listener on the order button
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //find the supplier phone number
                    EditText supplierPhoneView = findViewById(R.id.supplier_phone);
                    String supplierPhone = supplierPhoneView.getText().toString();

                    //create a new intent with the supplier phone number and open a dialer app
                    Intent dialIntent = new Intent();
                    dialIntent.setAction(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + supplierPhone));
                    startActivity(dialIntent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        // only continue with the back button press if the product hasn't changed
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        //if there are unsaved changes then warn the user via an alert dialog
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //user wants to discard changes to go ahead and quit the activity
                        finish();
                    }
                };

        //show the unsaved changes dialog
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void saveItem() {

        String productNameString = productNameEditText.getText().toString().trim();
        String productPriceValue = productPriceEditText.getText().toString().trim();
        String productQuantityValue = productQuantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        //create a new map of our values
        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(ProductEntry.COLUMN_PRICE, productPriceValue);
        values.put(ProductEntry.COLUMN_QUANTITY, productQuantityValue);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        //check if we're creating a new product or editing an existing product
        if (currentProductUri == null) {
            //insert our new row
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            //display a toast indicating if our insert was successful or not
            if (newUri == null) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.productSaveError, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.productSaved, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            //existing pet so update it's info
            int rowsUpdated = getContentResolver().update(currentProductUri, values, null, null);

            //show a toast message indicating if our update was successful or not
            if (rowsUpdated == 0) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.productUpdateError, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.productUpdated, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //if this is a new product then hide the "Delete" menu item.
        if (currentProductUri == null) {

            MenuItem menuItem = menu.findItem(R.id.delete_action);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu options from the res/menu/menu_edit.xml file.
        // this adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.save_action:
                if (validateInput()) {
                    //save product
                    saveItem();
                    //exit activity
                    finish();
                    return true;
                }
                break;
            // Respond to a click on the "Delete" menu option
            case R.id.delete_action:
                //show a dialog to confirm deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity) if nothing has changed
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                //there are unsaved changes to warn the user via alert dialog
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //user wants to discard changes to go ahead back to the Main Activity
                                NavUtils.navigateUpFromSameTask(ItemEditActivity.this);
                            }
                        };

                //show dialog to warn user about unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //define a projection that contains all the columns from our products table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        ////execute the ContentProvider query on a background thread
        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //if the cursor is null or doesn't have any rows, then quit
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //move the cursor to the first row and read it's data
        if (cursor.moveToFirst()) {
            //find the columns that we're interested
            int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            //get the values from the cursor
            String productName = cursor.getString(productNameColumnIndex);
            Double productPrice = cursor.getDouble(productPriceColumnIndex);
            int quantity = cursor.getInt(productQuantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            //update our views on screen with the database values
            productNameEditText.setText(productName);
            productPriceEditText.setText(Double.toString(productPrice));
            productQuantityEditText.setText(Integer.toString(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //clear input data fields
        productNameEditText.setText("");
        productPriceEditText.setText("");
        productQuantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }

    //prompt the user to confirm whether or not they want to discard their changes
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        //create an alert dialog then set the alert message and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discardMessage);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continueEditing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //user wants to continue editing so dismiss the alert and continue the activity
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //create and display our alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //prompt the user to confirm that they actually want to delete a product
    private void showDeleteConfirmationDialog() {
        //create an alert dialog then set the alert message and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteConfirmMessage);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //user confirmed deletion so proceed
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //user canceled deletion so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //delete a product from the database
    private void deleteProduct() {
        //only delete if it's an existing product
        if (currentProductUri != null) {
            //call the content resolver to delete the given product
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);

            //show a toast indicating whether or not the deletion was successful
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.deleteFailed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.deleteSuccess),
                        Toast.LENGTH_SHORT).show();
            }
        }

        //close the activity
        finish();
    }

    //make sure that all of the user input is valid
    private boolean validateInput() {

        //get the input values
        String productNameString = productNameEditText.getText().toString().trim();
        String productPriceValue = productPriceEditText.getText().toString().trim();
        String productQuantityValue = productQuantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        //indicates if there are errors or not
        boolean noErrors = true;

        //the error message we'll display
        String invalidMessage;

        //the error details we'll display
        StringBuilder errors = new StringBuilder();

        //check for a valid product name
        if (TextUtils.isEmpty(productNameString)) {
            noErrors = false;
            errors.append("\nProduct Name");
        }

        //check for a valid product price
        if (TextUtils.isEmpty(productPriceValue)) {
            noErrors = false;
            errors.append("\nProduct Price");
        }

        //check for a valid product quantity
        if (TextUtils.isEmpty(productQuantityValue)) {
            noErrors = false;
            errors.append("\nProduct Quantity");
        }

        //check for a valid supplier name
        if (TextUtils.isEmpty(supplierNameString)) {
            noErrors = false;
            errors.append("\nSupplier Name");
        }

        //check for a valid supplier phone
        if (TextUtils.isEmpty(supplierPhoneString)) {
            noErrors = false;
            errors.append("\nSupplier Phone");
        }

        //build the complete error message and show it in a Toast
        if (!noErrors) {
            invalidMessage = "The following fields are required but you left them blank:";
            Toast toast = Toast.makeText(getApplicationContext(), invalidMessage + errors.toString(), Toast.LENGTH_SHORT);
            toast.show();

        }

        return noErrors;
    }
}
