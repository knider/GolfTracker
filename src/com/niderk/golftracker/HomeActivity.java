package com.niderk.golftracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class HomeActivity extends Activity {

    /** Logging tag to be used for logging error. */
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();
	
	/** A helper object to manage the database. */
    private DatabaseHelper mDatabaseHelper = null;
	
	/** Used by <code>DatabaseTask</code> to perform various actions. */
    public enum RequestCode {
        insert
    }
	
	
	
	private static final int REQUEST_CODE_NEW_ENTRY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mDatabaseHelper = new DatabaseHelper(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	
	public void onCourseViewButtonClick(final View view) {           
        final Intent intent = new Intent(HomeActivity.this, SampleDatabaseActivity.class);
        startActivity(intent);

	}
	
	public void onCourseCreateButtonClick(final View view) {
		final Intent intent = new Intent(this, NewEntryActivity.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_ENTRY);
	}
	
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NEW_ENTRY && resultCode == RESULT_OK && data != null) {
        	// Extract name and address from sub-activity.
            final String name = data.getStringExtra(NewEntryActivity.ID_NAME);
            final String address = data.getStringExtra(NewEntryActivity.ID_ADDRESS);
            final String city = data.getStringExtra(NewEntryActivity.ID_CITY);
            final String zip = data.getStringExtra(NewEntryActivity.ID_ZIP);

            // Insert new entry into database.
            new DatabaseTask().execute(RequestCode.insert, mDatabaseHelper, name, address, city, zip);
            
        	final Intent intent = new Intent(HomeActivity.this, SampleDatabaseActivity.class);
            startActivity(intent);
        }
	}
	
	
	/** An <code>AsyncTask</code> that initialize the database. */
    public class DatabaseTask extends AsyncTask<Object, Void, Cursor> {

        /** {@inheritDoc} */
        @Override
        protected Cursor doInBackground(final Object... params) {
            if (params.length < 2 || !(params[0] instanceof RequestCode) || !(params[1] instanceof DatabaseHelper)) {
                Log.e(LOG_TAG, HomeActivity.this.getResources().getString(R.string.error_unknown_param));
                return null;
            }

            final RequestCode requestCode = (RequestCode) params[0];
            final DatabaseHelper databaseHelper = (DatabaseHelper) params[1];

            switch (requestCode) {
            
	            case insert:
	                if (params.length != 6 || !(params[2] instanceof String) || !(params[3] instanceof String)) {
	                    Log.e(LOG_TAG, HomeActivity.this.getResources().getString(R.string.error_unknown_param));
	                    return null;
	                }
	                final String insertName = (String) params[2];
	                final String insertAddress = (String) params[3];
	                final String insertCity = (String) params[4];
	                final String insertZip = (String) params[5];
	
	                // Insert new entry into database.
	                databaseHelper.insert(insertName, insertAddress, insertCity, insertZip);
	                break;
            
	



                default:
                    return null;
            }

            return databaseHelper.retrieveAllData();
        }
        
        /** {@inheritDoc} */
        /*@Override
        protected void onPostExecute(final Cursor cursor) {
            if (cursor != null) {
                updateListView(cursor);
            } else {
                Toast.makeText(SampleDatabaseActivity.this,
                        SampleDatabaseActivity.this.getResources().getString(R.string.error_generic),
                        Toast.LENGTH_SHORT).show();
            }
        }*/
    }

}
