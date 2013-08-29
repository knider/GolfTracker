// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.niderk.golftracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * A sample application that demonstrates how to read/write to a SQLiteDatabase and display its content in a ListView.
 * 
 * @version $Revision: #1 $, $Date: 2012/11/09 $
 */
public class SampleDatabaseActivity extends Activity {

    /** Logging tag to be used for logging error. */
    private static final String LOG_TAG = SampleDatabaseActivity.class.getSimpleName();
    
    public static final String EXTRA_ID = "com.niderk.golftracker.ID";
    public static final String EXTRA_NAME = "com.niderk.golftracker.NAME";
    public static final String EXTRA_ADDRESS = "com.niderk.golftracker.ADDRESS";
    public static final String EXTRA_CITY = "com.niderk.golftracker.CITY";
    public static final String EXTRA_ZIP = "com.niderk.golftracker.ZIP";
    
    /** Request code for NewEntryActivity. */
    private static final int REQUEST_CODE_NEW_ENTRY = 0;
    
    /** Request code for EditEntryActivity. */
    private static final int REQUEST_CODE_EDIT_ENTRY = 10;

    /** A helper object to manage the database. */
    private DatabaseHelper mDatabaseHelper = null;

    /** A list to display the content of the database. */
    private ListView mListview = null;

    /** Used by <code>DatabaseTask</code> to perform various actions. */
    public enum RequestCode {
        clear, close, delete, insert, open, reset, edit, view
    }

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new DatabaseHelper(this);

        mListview = (ListView) findViewById(R.id.listview_course);

        // Subscribe and handle the item click event.
        /*
        mListview.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position,
                    final long id) {
                // Get the item's data ID at the given position.
                final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                final int item_id = cursor.getInt(cursor.getColumnIndex("_id"));

                new DatabaseTask().execute(RequestCode.delete, mDatabaseHelper, item_id);

                return true;
            }
        });
        */
        
        mListview.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position,
                    final long id) {
                // Get the item's data ID at the given position.
                final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                final int item_id = cursor.getInt(cursor.getColumnIndex("_id"));

                final String[] intentItems = mDatabaseHelper.getForEdit(item_id);
                
                final Intent intent = new Intent(SampleDatabaseActivity.this, EditEntryActivity.class);
                intent.putExtra(EXTRA_ID, intentItems[0]);
                intent.putExtra(EXTRA_NAME, intentItems[1]);
                intent.putExtra(EXTRA_ADDRESS, intentItems[2]);
                intent.putExtra(EXTRA_CITY, intentItems[3]);
                intent.putExtra(EXTRA_ZIP, intentItems[4]);
                startActivityForResult(intent, REQUEST_CODE_EDIT_ENTRY);

                return true;
            }
        });
        
        
        mListview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent, final View view, final int position,
                    final long id) {
                // Get the item's data ID at the given position.
                final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                final int item_id = cursor.getInt(cursor.getColumnIndex("_id"));
                
                final String[] intentItems = mDatabaseHelper.getForEdit(item_id);
                
                final Intent intent = new Intent(SampleDatabaseActivity.this, EditEntryActivity.class);
                intent.putExtra(EXTRA_ID, intentItems[0]);
                intent.putExtra(EXTRA_NAME, intentItems[1]);
                intent.putExtra(EXTRA_ADDRESS, intentItems[2]);
                intent.putExtra(EXTRA_CITY, intentItems[3]);
                intent.putExtra(EXTRA_ZIP, intentItems[4]);
                startActivityForResult(intent, REQUEST_CODE_EDIT_ENTRY);

                return;
			}
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onResume() {
        super.onResume();

        // Initialize the database asynchronously.
        new DatabaseTask().execute(RequestCode.open, mDatabaseHelper);
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
          
        case R.id.menu_add:
                // Prompt user to enter a new entry.
                final Intent intent = new Intent(this, NewEntryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_ENTRY);
                return true;

            case R.id.menu_clear:
                // Clear the database.
                new DatabaseTask().execute(RequestCode.clear, mDatabaseHelper);
                return true;

            case R.id.menu_reset:
                // Clear the database and populate the default values.
                new DatabaseTask().execute(RequestCode.reset, mDatabaseHelper);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** {@inheritDoc} */
    @Override
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
        }
        
        if (requestCode == REQUEST_CODE_EDIT_ENTRY && resultCode == RESULT_OK && data != null) {
            // Extract name and address from sub-activity.
        	final String _id = data.getStringExtra(EditEntryActivity.ID_ID);
        	final String name = data.getStringExtra(EditEntryActivity.ID_NAME);
            final String address = data.getStringExtra(EditEntryActivity.ID_ADDRESS);
            final String city = data.getStringExtra(EditEntryActivity.ID_CITY);
            final String zip = data.getStringExtra(EditEntryActivity.ID_ZIP);

            // Insert new entry into database.
            new DatabaseTask().execute(RequestCode.edit, mDatabaseHelper, _id, name, address, city, zip);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onStop() {
        super.onStop();

        // Close any open database object.
        new DatabaseTask().execute(RequestCode.close, mDatabaseHelper);
    }

    /** Retrieve and display all data from the database. */
    private void updateListView(final Cursor cursor) {
        // A list of data fields
        final String[] from = { DatabaseHelper.KEY_NAME, DatabaseHelper.KEY_ADDRESS, DatabaseHelper.KEY_CITY, DatabaseHelper.KEY_ZIP };

        // A list of views available in listview_item.xml
        final int[] to = { R.id.name, R.id.address, R.id.city, R.id.zip };

        // Map the data fields to the corresponding view.
        // For API level 11 or above, use LoaderManager with a CursorLoader
        // instead.
        @SuppressWarnings("deprecation")
		final ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.listview_item, cursor, from, to);

        mListview.setAdapter(listAdapter);
    }

    /** An <code>AsyncTask</code> that initialize the database. */
    public class DatabaseTask extends AsyncTask<Object, Void, Cursor> {

        /** {@inheritDoc} */
        @Override
        protected Cursor doInBackground(final Object... params) {
            if (params.length < 2 || !(params[0] instanceof RequestCode) || !(params[1] instanceof DatabaseHelper)) {
                Log.e(LOG_TAG, SampleDatabaseActivity.this.getResources().getString(R.string.error_unknown_param));
                return null;
            }

            final RequestCode requestCode = (RequestCode) params[0];
            final DatabaseHelper databaseHelper = (DatabaseHelper) params[1];

            switch (requestCode) {
            
	            case insert:
	                if (params.length != 6 || !(params[2] instanceof String) || !(params[3] instanceof String)) {
	                    Log.e(LOG_TAG, SampleDatabaseActivity.this.getResources().getString(R.string.error_unknown_param));
	                    return null;
	                }
	                final String insertName = (String) params[2];
	                final String insertAddress = (String) params[3];
	                final String insertCity = (String) params[4];
	                final String insertZip = (String) params[5];
	
	                // Insert new entry into database.
	                databaseHelper.insert(insertName, insertAddress, insertCity, insertZip);
	                break;
            
	            case edit:
	                if (params.length != 7 || !(params[2] instanceof String) || !(params[3] instanceof String)  
	                	|| !(params[4] instanceof String) || !(params[5] instanceof String) || !(params[6] instanceof String)) {
	                	Log.e(LOG_TAG, SampleDatabaseActivity.this.getResources().getString(R.string.error_unknown_param));
	                    return null;
	                }
	                final String id = (String) params[2];
	                final String name = (String) params[3];
	                final String address = (String) params[4];
	                final String city = (String) params[5];
	                final String zip = (String) params[6];
	
	                // Modify entry in database.
	                databaseHelper.edit(id, name, address, city, zip);
	                break;
                case clear:
                    // Clear the database.
                    databaseHelper.clear();
                    break;

                case close:
                    // Close any open database object.
                    databaseHelper.close();
                    break;

                case delete:
                    if (params.length != 3 || !(params[2] instanceof Integer)) {
                        Log.e(LOG_TAG,
                                SampleDatabaseActivity.this.getResources().getString(R.string.error_unknown_param));
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

                case open:
                    try {
                        final boolean isNewDatabase = !databaseHelper.databaseExists();

                        // Create and/or open the database.
                        databaseHelper.getReadableDatabase();

                        if (isNewDatabase) {
                            // Populate the database with default values.
                            insertDefaultValues(databaseHelper);
                        }
                    } catch (final SQLiteException e) {
                        Log.e(LOG_TAG, e.getLocalizedMessage(), e);
                        return null;
                    }
                    break;

                case reset:
                    // Clear the database.
                    databaseHelper.clear();

                    // Populate the database with default values.
                    insertDefaultValues(databaseHelper);
                    break;

                default:
                    return null;
            }

            return databaseHelper.retrieveAllData();
        }

        /** {@inheritDoc} */
        @Override
        protected void onPostExecute(final Cursor cursor) {
            if (cursor != null) {
                updateListView(cursor);
            } else {
                Toast.makeText(SampleDatabaseActivity.this,
                        SampleDatabaseActivity.this.getResources().getString(R.string.error_generic),
                        Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Populate the database with default values.
         * 
         * @param db the database to modify.
         */
        private void insertDefaultValues(final DatabaseHelper databaseHelper) {
            databaseHelper.insert(SampleDatabaseActivity.this.getResources().getString(R.string.item1),
                    SampleDatabaseActivity.this.getResources().getString(R.string.address1),
                    SampleDatabaseActivity.this.getResources().getString(R.string.city1),
                    SampleDatabaseActivity.this.getResources().getString(R.string.zip1));
            databaseHelper.insert(SampleDatabaseActivity.this.getResources().getString(R.string.item2),
                    SampleDatabaseActivity.this.getResources().getString(R.string.address2),
                    SampleDatabaseActivity.this.getResources().getString(R.string.city2),
                    SampleDatabaseActivity.this.getResources().getString(R.string.zip2));
            databaseHelper.insert(SampleDatabaseActivity.this.getResources().getString(R.string.item3),
                    SampleDatabaseActivity.this.getResources().getString(R.string.address3),
                    SampleDatabaseActivity.this.getResources().getString(R.string.city3),
                    SampleDatabaseActivity.this.getResources().getString(R.string.zip3));
            databaseHelper.insert(SampleDatabaseActivity.this.getResources().getString(R.string.item4),
                    SampleDatabaseActivity.this.getResources().getString(R.string.address4),
                    SampleDatabaseActivity.this.getResources().getString(R.string.city4),
                    SampleDatabaseActivity.this.getResources().getString(R.string.zip4));
            databaseHelper.insert(SampleDatabaseActivity.this.getResources().getString(R.string.item5),
                    SampleDatabaseActivity.this.getResources().getString(R.string.address5),
                    SampleDatabaseActivity.this.getResources().getString(R.string.city5),
                    SampleDatabaseActivity.this.getResources().getString(R.string.zip5));
        }
    }
}
