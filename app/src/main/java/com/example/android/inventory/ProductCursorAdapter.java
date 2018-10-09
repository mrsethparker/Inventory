package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;

import java.text.NumberFormat;

public class ProductCursorAdapter extends CursorAdapter {

    private Context adapterContext;

    //construct a new ProductCursorAdapter
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
        adapterContext = context;
    }

    //make a new blank list item view but don't set any info yet
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    public void bindView(View view, final Context context, Cursor cursor) {
        //find fields to populate our inflated ListView template
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView priceView = (TextView) view.findViewById(R.id.price);
        TextView quantityView = (TextView) view.findViewById(R.id.quantity);

        //find the columns of the product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);
        int idIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);

        //extract attributes from our cursor
        String productName = cursor.getString(nameColumnIndex);
        Double productPrice = cursor.getDouble(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);
        final int productId = cursor.getInt(idIndex);
        final int quantity = Integer.parseInt(productQuantity);

        //get a locale specific currency number format so that we can display the price properly
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(context.getResources().getConfiguration().locale);

        //populate fields with our extracted attributes
        nameView.setText(productName);
        priceView.setText(currencyFormatter.format(productPrice));
        quantityView.setText(R.string.quantity_label);
        quantityView.append(productQuantity);

        /*
        //extract attributes from our cursor
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);
        final int productId = cursor.getInt(idIndex);
        final int quantity = Integer.parseInt(productQuantity);

        //get a locale specific currency number format so that we can display the price properly
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(context.getResources().getConfiguration().locale);

        //populate fields with our extracted attributes
        nameView.setText(productName);
        priceView.setText(productPrice);
        quantityView.setText(R.string.quantity_label);
        quantityView.append(productQuantity);
         */

        //hookup a click listener on the sale button
        view.findViewById(R.id.sale_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if we're already at 0
                if (quantity > 0) {
                    //we had product in inventory so go ahead and record the sale
                    MainActivity activity = (MainActivity) context;
                    activity.recordSale(productId, quantity);
                } else {
                    //if the product quantity is 0 then nothing to sell so display an out of stock toast
                    Toast toast = Toast.makeText(context, R.string.oos_message, Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
    }


}
