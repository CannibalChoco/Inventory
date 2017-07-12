package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL;
import static com.example.android.inventory.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.android.inventory.data.ProductContract.ProductEntry._ID;


public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * EditText field to enter the products name
     */
    private EditText nameEditText;

    /**
     * EditText field to enter the products price
     */
    private EditText priceEditText;

    /**
     * EditText field to enter the products quantity
     */
    private EditText quantityEditText;

    /**
     * EditText field to enter the suppliers email
     */
    private EditText suppliersEmailEditText;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri currentProductUri;

    /**
     * Identifier for the product data loader
     */
    private static final int PRODUCT_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        currentProductUri = getIntent().getData();
        if (currentProductUri == null) {
            setTitle(R.string.editor_activity_title_add_product);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_activity_title_edit_product);
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }

        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        quantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        suppliersEmailEditText = (EditText) findViewById(R.id.edit_product_supplier);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                deleteProduct();
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString.replace(".", ""));
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierEmailString = suppliersEmailEditText.getText().toString().trim();

        if (currentProductUri == null && TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierEmailString)) {
            finish();
            return;
        }

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, nameString);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmailString);

        if (currentProductUri == null) {
            Uri uri = getContentResolver().insert(CONTENT_URI, values);

            if (uri == null) {
                Toast.makeText(this, getString(R.string.message_product_not_saved),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.message_product_saved),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int productUpdated = getContentResolver().update(currentProductUri, values, null, null);

            if (productUpdated == 0) {
                Toast.makeText(this, getString(R.string.message_product_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.message_product_updated),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteProduct() {
        if (currentProductUri != null) {
            int productDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (productDeleted == 1) {
                Toast.makeText(this, getString(R.string.message_product_deleted),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.message_product_delete_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID, // always needed for the cursor that's being passed to a cursor adapter
                COLUMN_PRODUCT_NAME,
                COLUMN_PRODUCT_PRICE,
                COLUMN_PRODUCT_QUANTITY,
                COLUMN_PRODUCT_SUPPLIER_EMAIL};

        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        if(data.moveToFirst()){
            int nameColIndex = data.getColumnIndex(COLUMN_PRODUCT_NAME);
            int priceColIndex = data.getColumnIndex(COLUMN_PRODUCT_PRICE);
            int quantityColIndex = data.getColumnIndex(COLUMN_PRODUCT_QUANTITY);
            int supplierEmailColIndex = data.getColumnIndex(COLUMN_PRODUCT_SUPPLIER_EMAIL);

            String name = data.getString(nameColIndex);
            double price = data.getInt(priceColIndex) / 100.00;
            int quantity = data.getInt(quantityColIndex);
            String supplierEmail = data.getString(supplierEmailColIndex);

            nameEditText.setText(name);
            priceEditText.setText(String.format("%.2f", price));
            quantityEditText.setText(String.valueOf(quantity));
            suppliersEmailEditText.setText(supplierEmail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("0");
        suppliersEmailEditText.setText("");
    }
}
