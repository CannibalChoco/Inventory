package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.example.android.inventory.R.id.quantity;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;

public class ProductCursorAdapter extends CursorAdapter {

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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(quantity);

        int nameColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_PRODUCT_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        nameTextView.setText(name);

        double price = cursor.getInt(priceColumnIndex) / 100;
        priceTextView.setText(String.format("%.2f", price));

        int quantity = cursor.getInt(quantityColumnIndex);
        quantityTextView.setText(String.valueOf(quantity));

    }
}
