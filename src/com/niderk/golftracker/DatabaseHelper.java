// Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
package com.niderk.golftracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A <code>SQLiteOpenHelper</code> that manages the database.
 * 
 * @version $Revision: #1 $, $Date: 2012/11/09 $
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /** Key of the name field. */
    public static final String KEY_NAME = "name";

    /** Key of the address field. */
    public static final String KEY_ADDRESS = "address";
    
    /** Key of the city field. */
    public static final String KEY_CITY = "city";
    
    /** Key of the city field. */
    public static final String KEY_ZIP = "zip";

    /** Name of the database. */
    private static final String DATABASE_NAME = "golfcourse_database";

    /** Name of the table in the database. */
    private static final String TABLE_NAME = "golfcourses";

    /** A SQL query to create a table in the database. */
    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address TEXT, city TEXT, zip TEXT)";

    /** A SQL query to drop the table from the database. */
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    /** A SQL query to get all data from the table. */
    private static final String SQL_SELECT_ALL_DATA = "SELECT * FROM " + TABLE_NAME;

    /** Used to open or create the database */
    private Context mContext = null;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * 
     * @param context to use to open or create the database
     */
    public DatabaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, 1);
        mContext = context;
    }

    /** {@inheritDoc} */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    /** {@inheritDoc} */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // Recreate the database
        onCreate(db);
    }

    /** Clear the database. */
    public void clear() {
        final SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DROP_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * Tests whether the database file exists.
     * 
     * @return <code>true</code> if the database file exists, <code>false</code> otherwise.
     */
    public boolean databaseExists() {
        return mContext != null && mContext.getDatabasePath(DATABASE_NAME).exists();
    }

    /**
     * Delete an entry by its ID.
     * 
     * @param id the ID of the entry to delete.
     * @return <code>true</code> if the delete was successful, <code>false</code> otherwise.
     */
    public boolean deleteById(final int id) {
        if (getWritableDatabase().delete(TABLE_NAME, "_id=" + id, null) != 0) {
            return true;
        }
        return false;
    }

    /**
     * Insert a new entry to the database.
     * 
     * @param name the name of the new entry.
     * @param address the address of the new entry.
     * @param city the city of the new entry.
     * @param zip the zip code of the new entry.
     */
    public void insert(final String name, final String address, final String city, final String zip) {
        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_CITY, city);
        values.put(KEY_ZIP, zip);

        getWritableDatabase().insert(TABLE_NAME, null, values);
    }
    
    /**
     * Edit an entry in the database.
     * 
     * @param name the name of the edited entry.
     * @param address the address of the edited entry.
     */
    public void edit(final String _id, final String name, final String address, final String city, final String zip) {
        final ContentValues values = new ContentValues();
        
        values.put(KEY_NAME, name);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_CITY, city);
        values.put(KEY_ZIP, zip);

        getWritableDatabase().update(TABLE_NAME, values, "_id='" + _id + "'", null);
    }
    
    /**
     * View an entry by its ID.
     * 
     * @param id the ID of the entry to view.
     */
    
    public String[] getForEdit(final int _id) {
    	String[] value = new String[5];
        Cursor cursor = getReadableDatabase().query(TABLE_NAME, null, "_id=" + _id, null, null, null, null);

        cursor.moveToFirst();
    	// get  the  data into array,or class variable
        value[0] = cursor.getString(cursor.getColumnIndex("_id"));
    	value[1] = cursor.getString(cursor.getColumnIndex("name"));
        value[2] = cursor.getString(cursor.getColumnIndex("address"));
        value[3] = cursor.getString(cursor.getColumnIndex("city"));
        value[4] = cursor.getString(cursor.getColumnIndex("zip"));
        
        return value;
    }
    
    /**
     * View an entry by its ID.
     * 
     * @param id the ID of the entry to view.
     */
    
    public String[] view(final int _id) {
    	String[] value = new String[5];
        Cursor cursor = getReadableDatabase().query(TABLE_NAME, null, "_id=" + _id, null, null, null, "1");
        if (cursor.moveToFirst()) {
            do {
            	// get  the  data into array,or class variable
            	value[0] = cursor.getString(cursor.getColumnIndex("_id"));
            	value[1] = cursor.getString(cursor.getColumnIndex("name"));
                value[2] = cursor.getString(cursor.getColumnIndex("address"));
                value[3] = cursor.getString(cursor.getColumnIndex("city"));
                value[4] = cursor.getString(cursor.getColumnIndex("zip"));
               
            } while (cursor.moveToNext());
        }
        return value;
    }

    /**
     * Run a SQL to retrieve all the data (if any) from the database.
     * 
     * @return A <code>Cursor</code> object, which is positioned before the first entry.
     */
    public Cursor retrieveAllData() {
        return getReadableDatabase().rawQuery(SQL_SELECT_ALL_DATA, null);
    }
}
