package com.gmail.czzsunset.xinterphone.lib;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.gmail.czzsunset.xinterphone.Constants;
import com.gmail.czzsunset.xinterphone.lib.SimpleDatabaseHelper.SimpleTraceTable;


import java.util.HashMap;


/**
 * 
 * @author sunset
 * A provider class to provide interface to interact with database file 
 */
public class SimpleDatabaseProvider extends ContentProvider {

    // A projection map used to select columns from the database
    
    private static final String AUTHORITY = SimpleTraceTable.AUTHORITY;
    
    private final HashMap<String, String> mNotesProjectionMap;
    // Uri matcher to decode incoming URIs.
    private final UriMatcher mUriMatcher;   
    
    // The incoming URI matches the main table URI pattern
    private static final int TRACE_MAIN = 1;
    // The incoming URI matches the main table row ID URI pattern
    private static final int TRACE_MAIN_ID = 2;
    // The latest updated
    private static final int TRACE_LATEST = 3;

    private static final String TAG = "DatabaseProvider";    
    
    
    // Handle to a new SimpleDatabaseHelper.
    private SimpleDatabaseHelper mOpenHelper;    
    
    public SimpleDatabaseProvider() {
        
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, SimpleTraceTable.TABLE_NAME, TRACE_MAIN);
        mUriMatcher.addURI(AUTHORITY, SimpleTraceTable.TABLE_NAME + "/#", TRACE_MAIN_ID); 
        mUriMatcher.addURI(AUTHORITY, SimpleTraceTable.TABLE_NAME + "/latest", TRACE_LATEST);
        
        mNotesProjectionMap = new HashMap<String, String>();
        
        mNotesProjectionMap.put(SimpleTraceTable._ID, SimpleTraceTable._ID); 
        mNotesProjectionMap.put(SimpleTraceTable.MEMBER_LOCAL_ID, SimpleTraceTable.MEMBER_LOCAL_ID);
        mNotesProjectionMap.put(SimpleTraceTable.LATITUDE,   SimpleTraceTable.LATITUDE);
        mNotesProjectionMap.put(SimpleTraceTable.LONGITUDE,  SimpleTraceTable.LONGITUDE);        
        
    }
    
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new SimpleDatabaseHelper(getContext());
        // Assumes that any failures will be reported by a thrown exception.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        qb.setTables(SimpleTraceTable.TABLE_NAME);
        qb.setProjectionMap(mNotesProjectionMap);
        
        switch (mUriMatcher.match(uri)) {
            case TRACE_MAIN:
                // If the incoming URI is for main table.
                
                Log.d(TAG, "Query on table set main");
 
                break;

            case TRACE_MAIN_ID:
                // The incoming URI is for a single row.

                qb.appendWhere(SimpleTraceTable._ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs,
                        new String[] { uri.getLastPathSegment() });
                break;
                
            case TRACE_LATEST:
            	
                qb.appendWhere(SimpleTraceTable.IS_MOST_LATEST_UPDATE + "=1");
                
                break;
                
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        if (TextUtils.isEmpty(sortOrder)) {
//            sortOrder = SimpleTraceTable.DEFAULT_SORT_ORDER;
        }

       
        

        Cursor c = qb.query(db, projection, selection, selectionArgs,
                null /* no group */, null /* no filter */, null);

        Log.d(TAG, "get "+c.getCount() + " records");
        
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;        
        
    }

    @Override
    public String getType(Uri uri) {
        switch(mUriMatcher.match(uri)){
            case TRACE_MAIN:
                return SimpleTraceTable.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (mUriMatcher.match(uri) != TRACE_MAIN) {
            // Can only insert into to main URI.
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;

        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

//        if (values.containsKey(SimpleTraceTable.DISTANCE) == false) {
//            values.put(SimpleTraceTable.DISTANCE, 999);
//        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId = db.insert(SimpleTraceTable.TABLE_NAME, null, values);

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(SimpleTraceTable.CONTENT_ID_URI_BASE, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
