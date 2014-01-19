package com.gmail.czzsunset.xinterphone.lib;

import java.util.HashMap;

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

import com.gmail.czzsunset.xinterphone.lib.DatabaseHelper.MatesTable;
import com.gmail.czzsunset.xinterphone.lib.DatabaseHelper.TraceTable;


/**
 * 
 * @author sunset
 * A provider class to provide interface to interact with database file 
 */
public class DatabaseProvider extends ContentProvider {

    // A projection map used to select columns from the database
    
    private static final String AUTHORITY = TraceTable.AUTHORITY;
    
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
    
    
    // Handle to a new DatabaseHelper.
    private DatabaseHelper mOpenHelper;    
    
    public DatabaseProvider() {
        
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, TraceTable.TABLE_NAME, TRACE_MAIN);
        mUriMatcher.addURI(AUTHORITY, TraceTable.TABLE_NAME + "/#", TRACE_MAIN_ID); 
        mUriMatcher.addURI(AUTHORITY, TraceTable.TABLE_NAME + "/latest", TRACE_LATEST);
        
        mNotesProjectionMap = new HashMap<String, String>();
        
        mNotesProjectionMap.put(MatesTable.TABLE_NAME + "." + MatesTable._ID, 
                                MatesTable.TABLE_NAME + "." + MatesTable._ID);
        mNotesProjectionMap.put(MatesTable.NAME, MatesTable.NAME);
        mNotesProjectionMap.put(TraceTable.TABLE_NAME + "." + TraceTable.LATITUDE, 
                                                                    TraceTable.LATITUDE);
        mNotesProjectionMap.put(TraceTable.TABLE_NAME + "." + TraceTable.LONGITUDE, 
                                                                    TraceTable.LONGITUDE);        
        
    }
    
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        // Assumes that any failures will be reported by a thrown exception.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        
        switch (mUriMatcher.match(uri)) {
            case TRACE_MAIN:
                // If the incoming URI is for main table.
                qb.setTables(TraceTable.TABLE_NAME);
                Log.d(TAG, "Query on table set main");
                qb.setProjectionMap(mNotesProjectionMap);
                break;

            case TRACE_MAIN_ID:
                // The incoming URI is for a single row.

                qb.setTables(TraceTable.TABLE_NAME);
                qb.setProjectionMap(mNotesProjectionMap);
                qb.appendWhere(TraceTable._ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs,
                        new String[] { uri.getLastPathSegment() });
                break;
            case TRACE_LATEST:
                                
                
                qb.setTables(MatesTable.TABLE_NAME 
                        + " LEFT OUTER JOIN " + TraceTable.TABLE_NAME 
                        + " ON " + MatesTable.TABLE_NAME + "." + MatesTable.GLOBAL_ID 
                        + " = " + TraceTable.TABLE_NAME + "." + TraceTable.MEMBER_GLOBAL_ID);
                
                qb.setProjectionMap(mNotesProjectionMap);
                qb.appendWhere(TraceTable.IS_MOST_LATEST_UPDATE + "=1 OR " +
                                TraceTable.IS_MOST_LATEST_UPDATE + " is NULL");
                
                break;
                
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        if (TextUtils.isEmpty(sortOrder)) {
//            sortOrder = TraceTable.DEFAULT_SORT_ORDER;
        }

       
        

        Cursor c = qb.query(db, projection, selection, selectionArgs,
                null /* no group */, null /* no filter */, null);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;        
        
    }

    @Override
    public String getType(Uri uri) {
        switch(mUriMatcher.match(uri)){
            case TRACE_MAIN:
                return TraceTable.CONTENT_TYPE;
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

//        if (values.containsKey(TraceTable.DISTANCE) == false) {
//            values.put(TraceTable.DISTANCE, 999);
//        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId = db.insert(TraceTable.TABLE_NAME, null, values);

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(TraceTable.CONTENT_ID_URI_BASE, rowId);
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
