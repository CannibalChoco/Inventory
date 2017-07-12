package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.ProductContract.ProductEntry;

import com.example.android.inventory.data.ProductDbHelper;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;

import static com.example.android.inventory.R.id.fab;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL;
import static com.example.android.inventory.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.android.inventory.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry._ID;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    /** Tag for the log messages */
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private ProductDbHelper dbHelper = new ProductDbHelper(this);

    private ProductCursorAdapter adapter;

    private int PRODUCT_DB_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        dbHelper = new ProductDbHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "TEST: launching EditorActivity...");
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView productListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        adapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(adapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.v(LOG_TAG, "TEST: launching EditorActivity...");
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_DB_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                getContentResolver().delete(CONTENT_URI, null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertDummyProduct() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, "socks");
        values.put(COLUMN_PRODUCT_PRICE, 234);
        values.put(COLUMN_PRODUCT_QUANTITY, 4);
        values.put(COLUMN_PRODUCT_SUPPLIER_EMAIL, "supplier@emai.com");

        Uri uri = getContentResolver().insert(CONTENT_URI, values);
        //Toast.makeText(this, "" + String.valueOf(uri), Toast.LENGTH_SHORT).show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                _ID, // always needed for the cursor that's being passed to a cursor adapter
                COLUMN_PRODUCT_NAME,
                COLUMN_PRODUCT_PRICE,
                COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
