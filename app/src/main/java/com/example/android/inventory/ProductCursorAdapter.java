package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.android.inventory.R.id.quantity;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.android.inventory.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.android.inventory.data.ProductContract.ProductEntry._ID;

public class ProductCursorAdapter extends CursorAdapter {

    private String LOG_TAG = ProductCursorAdapter.class.getSimpleName();

    /**
     * The constructor
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final int position = cursor.getPosition();

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(quantity);

        int idColIndex = cursor.getColumnIndex(_ID);
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY);

        final long id = cursor.getLong(idColIndex);
        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getInt(priceColumnIndex) / 100.00;
        final int quantity = cursor.getInt(quantityColumnIndex);

        // format how price is displayed
        String decimalPriceString = String.format("%.2f", price);
        String formattedPriceString = "$".concat(decimalPriceString);
        priceTextView.setText(formattedPriceString);
        nameTextView.setText(name);

        quantityTextView.setText(String.valueOf(quantity));

        Button saleButton = (Button) view.findViewById(R.id.sale);
        saleButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0){
                    ContentValues values = new ContentValues();

                    values.put(COLUMN_PRODUCT_QUANTITY, quantity - 1);

                    Uri currentProductUri = ContentUris.withAppendedId(CONTENT_URI, id);

                    context.getContentResolver().update(currentProductUri, values, null, null);
                    context.getContentResolver().notifyChange(CONTENT_URI, null);
                }else{
                    Toast.makeText(context, "" + R.string.message_out_of_stock, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
