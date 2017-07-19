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

import butterknife.BindView;
import butterknife.ButterKnife;

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

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

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
        viewHolder.priceTextView.setText(formattedPriceString);
        viewHolder.nameTextView.setText(name);

        viewHolder.quantityTextView.setText(String.valueOf(quantity));

        viewHolder.saleButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    ContentValues values = new ContentValues();

                    values.put(COLUMN_PRODUCT_QUANTITY, quantity - 1);

                    Uri currentProductUri = ContentUris.withAppendedId(CONTENT_URI, id);

                    context.getContentResolver().update(currentProductUri, values, null, null);
                    context.getContentResolver().notifyChange(CONTENT_URI, null);
                } else {
                    Toast.makeText(context, "" + R.string.message_out_of_stock, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    static class ViewHolder {
        @BindView(R.id.name) TextView nameTextView;
        @BindView(R.id.price) TextView priceTextView;
        @BindView(R.id.quantity) TextView quantityTextView;
        @BindView(R.id.sale) TextView saleButton;

        public  ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
