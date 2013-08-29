// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.niderk.golftracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * An <code>Activity</code> used to prompt the user to enter a name and address.
 * 
 * @version $Revision: #1 $, $Date: 2012/11/09 $
 */
public class NewEntryActivity extends Activity {

    /** ID used to extract the name from the <code>Intent</code> in the <code>onActivityResult</code> method. */
    public final static String ID_NAME = "name";

    /** ID used to extract the address from the <code>Intent</code> in the <code>onActivityResult</code> method. */
    public final static String ID_ADDRESS = "address";
    
    /** ID used to extract the address from the <code>Intent</code> in the <code>onActivityResult</code> method. */
    public final static String ID_CITY = "city";
    
    /** ID used to extract the address from the <code>Intent</code> in the <code>onActivityResult</code> method. */
    public final static String ID_ZIP = "zip";

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
    }

    /**
     * Pass user's input back to parent to process.
     * 
     * @param view the button being clicked on.
     */
    public void onSaveButtonClick(final View view) {
        final EditText editText_name = (EditText) findViewById(R.id.editText_name);
        final EditText editText_address = (EditText) findViewById(R.id.editText_address);
        final EditText editText_city = (EditText) findViewById(R.id.editText_city);
        final EditText editText_zip = (EditText) findViewById(R.id.editText_zip);

        // Ensure the name is not empty.
        final String name = editText_name.getText().toString().trim();
        if (name.length() == 0) {
            final String errorMessage = getResources().getString(R.string.error_name_is_empty);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        final String address = editText_address.getText().toString().trim();
        final String city = editText_city.getText().toString().trim();
        final String zip = editText_zip.getText().toString().trim();

        // Pass the name and address back to the parent activity
        final Intent intent = new Intent();
        intent.putExtra(ID_NAME, name);
        intent.putExtra(ID_ADDRESS, address);
        intent.putExtra(ID_CITY, city);
        intent.putExtra(ID_ZIP, zip);
        setResult(RESULT_OK, intent);
        finish();
    }
}
