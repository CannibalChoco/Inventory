package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

import static com.example.android.inventory.R.string.delete;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;
import static com.example.android.inventory.data.ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL;
import static com.example.android.inventory.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.android.inventory.data.ProductContract.ProductEntry._ID;


public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /**
     * Button to delete the product entry
     */
    @BindView(R.id.delete_entry)
    Button deleteEntryButton;

    /**
     * Button to open email to order more
     */
    @BindView(R.id.order_more)
    Button orderMoreButton;

    /**
     * ImageView field upload or display products image
     */
    @BindView(R.id.image)
    ImageView productsImageView;

    /**
     * EditText field to enter the products name
     */
    @BindView(R.id.edit_product_name)
    EditText nameEditText;

    /**
     * EditText field to enter the products price
     */
    @BindView(R.id.edit_product_price)
    EditText priceEditText;

    /**
     * TextView field to enter the products quantity
     */
    @BindView(R.id.quantity_text)
    TextView quantityText;

    /**
     * Spinner to select either to subtract or add to products quantity
     */
    @BindView(R.id.spinner_edit_quantity)
    Spinner editQuantitySpinner;

    /**
     * EditText field to enter amount by which to edit the quantity
     */
    @BindView(R.id.edit_product_quantity_by)
    EditText editQuantityEditText;

    /**
     * EditText field to enter the suppliers email
     */
    @BindView(R.id.edit_product_supplier)
    EditText suppliersEmailEditText;

    /**
     * Decrement product quantity by one
     */
    @BindView(R.id.decrement_by_one)
    Button decrementByOne;

    /**
     * Increment product quantity by one
     */
    @BindView(R.id.increment_by_one)
    Button incrementByOne;

    /**
     * TextView for when there is no image added
     */
    @BindView(R.id.no_image_text)
    TextView noImageTextView;

    /**
     * Edit quantity variables to track users choice
     */
    private boolean decrementByX = false;
    private boolean incrementByX = false;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri currentProductUri;

    /**
     * Identifier for the product data loader
     */
    private static final int PRODUCT_LOADER = 0;

    /**
     * Identifier for image request
     */
    static final int REQUEST_IMAGE_GET = 1;

    /**
     * Products image Uri converted to String
     */
    private String imageUriString;

    /**
     * Store products name
     */
    private String name;

    /**
     * Store suppliers email
     */
    private String supplierEmail;

    /**
     * Track whether the user has edited product info
     */
    private boolean productHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ButterKnife.bind(this);

        currentProductUri = getIntent().getData();
        if (currentProductUri == null) {
            setTitle(R.string.editor_activity_title_add_product);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_activity_title_edit_product);
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }

        priceEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});

        orderMoreButton.setText(R.string.button_order_more);
        orderMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supplierEmail != null) {
                    orderMore();
                }
            }
        });

        deleteEntryButton.setText(R.string.button_delete);
        deleteEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        if (currentProductUri == null) {
            deleteEntryButton.setVisibility(View.INVISIBLE);
        }

        noImageTextView.setText(R.string.no_image_text_view);

        deleteEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        editQuantitySpinner.setOnTouchListener(touchListener);
        editQuantityEditText.setOnTouchListener(touchListener);
        suppliersEmailEditText.setOnTouchListener(touchListener);

        productsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        decrementByOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productHasChanged = true;

                int quantity;
                if (currentProductUri != null) {
                    String quantityString = quantityText.getText().toString().trim();
                    quantity = Integer.parseInt(quantityString);
                } else {
                    quantity = 0;
                }

                if (quantity > 0) {
                    quantity--;
                }

                quantityText.setText(String.valueOf(quantity));
            }
        });

        incrementByOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productHasChanged = true;

                int quantity;
                String quantityString = quantityText.getText().toString().trim();
                if (TextUtils.isEmpty(quantityString)) {
                    quantity = 0;
                } else {
                    quantity = Integer.parseInt(quantityString);
                }
                quantity++;

                quantityText.setText(String.valueOf(quantity));
            }
        });

        editQuantityEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editQuantityBy();
            }
        });

        setupSpinner();
    }

    /**
     * Helper method to edit the quantity
     */
    private void editQuantityBy() {
        int quantity;
        if (currentProductUri != null) {
            String quantityString = quantityText.getText().toString().trim();
            quantity = Integer.parseInt(quantityString);
        } else {
            quantity = 0;
        }

        String editQuantityString = editQuantityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(editQuantityString) && quantity == 0) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.message_no_product_quantity),
                    Toast.LENGTH_SHORT).show();
        } else {
            int editQuantity = Integer.parseInt(editQuantityString);

            // subtract
            if (decrementByX && !incrementByX) {
                if (quantity > editQuantity) {
                    quantity -= editQuantity;
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.message_invalid_amount_to_subtract, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            // add
            if (!decrementByX && incrementByX) {
                quantity += editQuantity;
            }

            quantityText.setText(String.valueOf(quantity));
        }
    }

    /**
     * Helper method to format the price string so it can be converted to int correctly
     */

    private String formatPriceString(String priceString) {
        if (priceString.contains(",")) {
            priceString = priceString.replace(",", ".");
        }

        if (priceString.contains(".")) {
            int integerPlaces = priceString.indexOf(".");

            if (integerPlaces == 0) {
                return priceString.replace(".", "");
            }

            int decimalPlaces = priceString.length() - integerPlaces - 1;

            // if there is only one decimal plac, add one
            if (decimalPlaces == 1) {
                return priceString.concat("0").replace(".", "");
            } else {
                // if there are too many, crop it
                return priceString.substring(0, integerPlaces + 2).replace(".", "");
            }
        } else {
            // if there are no decimal places, add two
            return priceString.concat("00");
        }
    }


    /**
     * Helper method to select image from gallery.
     * Code taken form https://developer.android.com/guide/components/intents-common.html#Storage
     */
    private void selectImage() {
        Intent intent;
        if(Build.VERSION.SDK_INT >= 19 ){
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        }else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    /**
     * Helper method to order more products.
     */
    private void orderMore() {
        String messageTemplate = getString(R.string.email_order_more);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + supplierEmail));
        intent.putExtra(Intent.EXTRA_SUBJECT, name);
        intent.putExtra(Intent.EXTRA_TEXT, messageTemplate);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Set the Image Uri
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            productsImageView.setImageURI(imageUri);
            imageUriString = imageUri.toString();
            productHasChanged = true;
            noImageTextView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter editQuantitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_edit_quantity_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        editQuantitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        editQuantitySpinner.setAdapter(editQuantitySpinnerAdapter);

        // Set the integer mSelected to the constant values
        editQuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.spinner_subtract))) {
                        decrementByX = true;
                        incrementByX = false;
                    } else {
                        incrementByX = true;
                        decrementByX = false;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                decrementByX = false;
                incrementByX = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (currentProductUri == null) {
            MenuItem actionDelete = menu.findItem(R.id.action_delete);
            actionDelete.setVisible(false);
        }

        return true;
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
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();

        String quantityString = quantityText.getText().toString().trim();
        String supplierEmailString = suppliersEmailEditText.getText().toString().trim();

        if (currentProductUri == null && TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(supplierEmailString)) {
            finish();
            return;
        } else if (imageUriString == null) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.message_no_image),
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.message_no_product_name),
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.message_no_product_price),
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.message_no_product_quantity),
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(supplierEmailString)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.message_no_supplier_email),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            priceString = formatPriceString(priceString);
            Integer price = Integer.parseInt(priceString);

            int quantity = Integer.parseInt(quantityString);

            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCT_NAME, nameString);
            values.put(COLUMN_PRODUCT_PRICE, price);
            values.put(COLUMN_PRODUCT_QUANTITY, quantity);
            values.put(COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmailString);
            values.put(COLUMN_PRODUCT_IMAGE, imageUriString);

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

            finish();
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
                COLUMN_PRODUCT_SUPPLIER_EMAIL,
                COLUMN_PRODUCT_IMAGE};

        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            int nameColIndex = data.getColumnIndex(COLUMN_PRODUCT_NAME);
            int priceColIndex = data.getColumnIndex(COLUMN_PRODUCT_PRICE);
            int quantityColIndex = data.getColumnIndex(COLUMN_PRODUCT_QUANTITY);
            int supplierEmailColIndex = data.getColumnIndex(COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int imageStringColIndex = data.getColumnIndex(COLUMN_PRODUCT_IMAGE);

            name = data.getString(nameColIndex);
            double price = data.getInt(priceColIndex) / 100.00;
            int quantity = data.getInt(quantityColIndex);
            supplierEmail = data.getString(supplierEmailColIndex);
            imageUriString = data.getString(imageStringColIndex);
            Uri imageUri = Uri.parse(imageUriString);

            nameEditText.setText(name);
            priceEditText.setText(String.format("%.2f", price));
            quantityText.setText(String.valueOf(quantity));
            suppliersEmailEditText.setText(supplierEmail);
            productsImageView.setImageURI(imageUri);
        }

        noImageTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityText.setText("0");
        suppliersEmailEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}