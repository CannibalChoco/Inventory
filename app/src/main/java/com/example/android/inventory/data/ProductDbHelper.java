package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL;
import static com.example.android.inventory.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry._ID;


public class ProductDbHelper extends SQLiteOpenHelper {

    /**
     * if the schema is changed, DATABASE_VERSION must be incremented
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * name of the database file
     */
    private static final String DATABASE_NAME = "inventory.db";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the db is created for the first time
     *
     * @param db the object that is representing the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + COLUMN_PRODUCT_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_IMAGE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
