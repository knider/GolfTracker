// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.niderk.golftracker;

import com.niderk.golftracker.SampleDatabaseActivity.RequestCode;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * An <code>Activity</code> used to prompt the user to enter a name and address.
 * 
 * @version $Revision: #1 $, $Date: 2012/11/09 $
 */
public class EditEntryActivity extends Activity {

    /** Logging tag to be used for logging error. */
    private static final String LOG_TAG = EditEntryActivity.class.getSimpleName();
    
	/** ID used to extract the id from the <code>Intent</code> in the <code>onActivityResult</code> method. */
    public final static String ID_ID = "_id";
	
	/** ID used to extract the name from the <code>Intent</code> in the <code>onActivityResult</code> method. */
    public final static String ID_NAME = "name";
    
    /** ID used to extract the address from the <code>Intent</code> in the <code>onActivityResult</code> method. */
    public final static String ID_ADDRESS = "address";
    
    /** ID used to extract the city from the <code>Intent</code> in the <code>onActivityResult</code> method. */
    public final static String ID_CITY = "city";
    
    /** ID used to extract the zip from the <code>Intent</code> in the <code>onActivityResult</code> method. */
    public final static String ID_ZIP = "zip";
    
	/** A helper object to manage the database. */
    private DatabaseHelper mDatabaseHelper = null;
    
    public String _id = null;
    
    

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        mDatabaseHelper = new DatabaseHelper(this);
        
        Intent intent = getIntent();
        //final String extra_id = intent.getStringExtra(SampleDatabaseActivity.EXTRA_ID);
        _id = intent.getStringExtra(SampleDatabaseActivity.EXTRA_ID);
        final String extra_name = intent.getStringExtra(SampleDatabaseActivity.EXTRA_NAME);
        final String extra_address = intent.getStringExtra(SampleDatabaseActivity.EXTRA_ADDRESS);
        final String extra_city = intent.getStringExtra(SampleDatabaseActivity.EXTRA_CITY);
        final String extra_zip = intent.getStringExtra(SampleDatabaseActivity.EXTRA_ZIP);
 
        /*TextView textView_id = (TextView) findViewById(R.id.textView_id);
        textView_id.setTextSize(12);
        textView_id.setText(extra_id);*/

        EditText editText_name = (EditText) findViewById(R.id.editText_name);
        EditText editText_address = (EditText) findViewById(R.id.editText_address);
        EditText editText_city = (EditText) findViewById(R.id.editText_city);
        EditText editText_zip = (EditText) findViewById(R.id.editText_zip);
        
        editText_name.setText(extra_name);
        editText_address.setText(extra_address);
        editText_city.setText(extra_city);
        editText_zip.setText(extra_zip);
        
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit, menu);
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
          
        case R.id.menu_delete:
        	int idNum = 0;
        	try {
        	    idNum = Integer.parseInt(_id);
        	} catch(NumberFormatException nfe) { 
        		Log.e(LOG_TAG, nfe.getLocalizedMessage(), nfe);
		        return false;
        	}
        	new DatabaseTask().execute(RequestCode.delete, mDatabaseHelper, idNum);
        	
        	Toast.makeText(this, getResources().getString(R.string.delete_ok), Toast.LENGTH_SHORT).show();
        	
        	final Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        	return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    /** An <code>AsyncTask</code> that initialize the database. */
    public class DatabaseTask extends AsyncTask<Object, Void, Cursor> {

        /** {@inheritDoc} */
        @Override
        protected Cursor doInBackground(final Object... params) {
            if (params.length < 2) {
                Log.e(LOG_TAG, "params too short");
                return null;
            }
            if (!(params[0] instanceof RequestCode)) {
                Log.e(LOG_TAG, "not an instance of RequestCode");
                return null;
            }
            if (!(params[1] instanceof DatabaseHelper)) {
                Log.e(LOG_TAG, "not an instance of DatabaseHelper");
                return null;
            }

            final RequestCode requestCode = (RequestCode) params[0];
            final DatabaseHelper databaseHelper = (DatabaseHelper) params[1];

            switch (requestCode) {
				case delete:
				    if (params.length != 3 || !(params[2] instanceof Integer)) {
				        Log.e(LOG_TAG,
				                EditEntryActivity.this.getResources().getString(R.string.error_unknown_param));
				        return null;
				    }
				
				    try {
				        final int item_id = Integer.parseInt(params[2].toString());
				
				        // Delete the entry from the database.
				        databaseHelper.deleteById(item_id);
				    } catch (final NumberFormatException e) {
				        Log.e(LOG_TAG, e.getLocalizedMessage(), e);
				        return null;
				    }
				    break;
				
				default:
                    return null;
            }

            return databaseHelper.retrieveAllData();
        }
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
        //final TextView textView_id = (TextView) findViewById(R.id.textView_id);
        
        //final String _id = textView_id.getText().toString();

        // Ensure the name is not empty.
        final String name = editText_name.getText().toString().trim();
        if (name.length() == 0) {
            final String errorMessage = getResources().getString(R.string.error_name_is_empty);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        final String address = editText_address.getText().toString();
        final String city = editText_city.getText().toString();
        final String zip = editText_zip.getText().toString();

        // Pass the name and address back to the parent activity
        final Intent intent = new Intent();
        //Toast.makeText(this, "ID = "+ _id, Toast.LENGTH_SHORT).show();
        intent.putExtra(ID_ID, _id);
        intent.putExtra(ID_NAME, name);
        intent.putExtra(ID_ADDRESS, address);
        intent.putExtra(ID_CITY, city);
        intent.putExtra(ID_ZIP, zip);
        setResult(RESULT_OK, intent);
        finish();
    }
}
