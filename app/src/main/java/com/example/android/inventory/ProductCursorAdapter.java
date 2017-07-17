package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;
import com.example.android.inventory.data.ProductDbHelper;
import com.example.android.inventory.data.ProductProvider;

import static android.R.attr.data;
import static android.R.attr.id;
import static com.example.android.inventory.R.id.quantity;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.android.inventory.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.android.inventory.data.ProductContract.ProductEntry.TABLE_NAME;

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

        int nameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getInt(priceColumnIndex) / 100.00;
        int quantity = cursor.getInt(quantityColumnIndex);

        priceTextView.setText(String.format("%.2f", price));
        nameTextView.setText(name);
        quantityTextView.setText(String.valueOf(quantity));

        Button saleButton = (Button) view.findViewById(R.id.sale);
        saleButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: get this to work properly
                cursor.moveToPosition(position);
                ContentValues values = new ContentValues();

                int quantityColIndex = cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY);
                int quantity = cursor.getInt(quantityColIndex);
                Log.i(LOG_TAG, "TEST: quantity = " + quantity);

                if ( quantity > 0){
                    int updatedQuantity = quantity - 1;

                    values.put(COLUMN_PRODUCT_QUANTITY, updatedQuantity);
                    Log.i(LOG_TAG, "TEST: new quantity = " + updatedQuantity);

                    Uri currentProductUri = ContentUris.withAppendedId(CONTENT_URI, position);

                    int productUpdated = context.getContentResolver().update(currentProductUri, values, null, null);
                    Log.i(LOG_TAG, "TEST: product updated: " + productUpdated);
                }
                context.getContentResolver().notifyChange(CONTENT_URI, null);
            }
        });
    }
}
