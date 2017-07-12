package com.example.android.inventory;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract;

import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL;
import static com.example.android.inventory.data.ProductContract.ProductEntry.CONTENT_URI;


public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the products name */
    private EditText nameEditText;

    /** EditText field to enter the products price */
    private EditText priceEditText;

    /** EditText field to enter the products quantity */
    private EditText quantityEditText;

    /** EditText field to enter the suppliers email */
    private EditText suppliersEmailEditText;

    /** Content URI for the existing product (null if it's a new product) */
    private Uri currentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        quantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        suppliersEmailEditText = (EditText) findViewById(R.id.edit_product_supplier);
    }

    @Override
    public  void onBackPressed(){
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

                return true;
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct (){
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString.replace(".", ""));
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierEmailString = suppliersEmailEditText.getText().toString().trim();

        if(currentProductUri == null && TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierEmailString)){
            finish();
            return;
        }

        int quantity = 0;
        if(!TextUtils.isEmpty(quantityString)){
            quantity = Integer.parseInt(quantityString);
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, nameString);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmailString);

        if(currentProductUri == null){
            Uri uri = getContentResolver().insert(CONTENT_URI, values);

            if(uri == null){
                Toast.makeText(this, getString(R.string.message_product_not_saved),
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, getString(R.string.message_product_saved),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
