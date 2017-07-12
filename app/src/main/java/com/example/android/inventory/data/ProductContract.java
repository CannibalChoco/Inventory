package com.example.android.inventory.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.text.style.TtsSpan.GENDER_FEMALE;
import static android.text.style.TtsSpan.GENDER_MALE;

public class ProductContract {

    private ProductContract(){}

    /**
     * CONTENT_AUTHORITY is the name for the entire content provider
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    /**
     * Base of all URI's
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_PRODUCTS = "products";

    /**
     * Inner Class that defines constants for the product DB table
     */
    public static abstract class ProductEntry implements BaseColumns{
        /**
         * The content URI to access the product data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * Name of database table for products
         */
        public static final String TABLE_NAME = "products";

        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NAME= "name";

        /**
         * Price of the product.
         *
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_PRICE= "price";

        /**
         * Quantity of the product.
         *
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_QUANTITY= "quantity";

        /**
         * Image of the product.Product suppliers email
         *
         * Type: STRING
         */
        public static final String COLUMN_PRODUCT_SUPPLIER_EMAIL= "supplier";

        /**
         * Image of the product.
         *
         * Type: BLOB
         */
        public static final String COLUMN_PRODUCT_IMAGE= "image";

        /**
         * Returns whether or not the given quantity is valid
         */
        public static boolean isValidPrice(int price) {
            if (price >= 0) {
                return true;
            }
            return false;
        }

        /**
         * Returns whether or not the given quantity is valid
         */
        public static boolean isValidQuantity(int quantity) {
            if (quantity >= 0) {
                return true;
            }
            return false;
        }
    }
}
