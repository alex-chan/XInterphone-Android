package com.gmail.czzsunset.xinterphone.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.gmail.czzsunset.xinterphone.Constants;
import com.gmail.czzsunset.xinterphone.model.User;

import java.util.List;


/**
 * 
 * @author sunset
 * This class helps open, create, and upgrade the database file
 */

public class SimpleDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "simple_xinterphone.db";
    private static final int DATABASE_VERSION = 2;
    
    private static final String TAG = "SimpleDatabaseHelper";
    
    public SimpleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); 
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createTraceTable(db);
        
        test_addSomeone(db);
        
    }
    
    
    private void createTraceTable(SQLiteDatabase db){

        db.execSQL("CREATE TABLE " + SimpleTraceTable.TABLE_NAME + " ("
                + SimpleTraceTable._ID + " INTEGER PRIMARY KEY," 
                + SimpleTraceTable.MEMBER_LOCAL_ID + " INTEGER,"                
                + SimpleTraceTable.LATITUDE   + " DOUBLE ,"
                + SimpleTraceTable.LONGITUDE + " DOUBLE,"
                + SimpleTraceTable.IS_MOST_LATEST_UPDATE + " INTEGER"
                + ");" );        
    }

    private void test_addSomeone(SQLiteDatabase db){
    	
    	appendTraceRecordWithDb(db, 1, 0, 22.58610, 113.94620);
    	appendTraceRecordWithDb(db, 2, 0, 22.58620, 113.94602);
    	appendTraceRecordWithDb(db, 3, 0, 22.58615, 113.94625);    	

    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version "+ oldVersion + " to " 
                    + newVersion + ", which will destroy all old data"  );
        
        db.execSQL("DROP TABLE IF EXISTS " + SimpleTraceTable.TABLE_NAME);
        
        onCreate(db);
    }
    
    @Override
    public void onDowngrade(SQLiteDatabase db, int old, int newV){
    	onUpgrade(db, old, newV);
    }
    
    /**
     * Set the IS_MOST_LATEST_LOCATION flag of the member ID to False(0)
     */
    private void invalidMostLatestLocationRecord(SQLiteDatabase db, int memberId){
        
        
        ContentValues values = new ContentValues();
        values.put(SimpleTraceTable.IS_MOST_LATEST_UPDATE, 0);
        
        String whereClause = SimpleTraceTable.MEMBER_LOCAL_ID + "=?";
        String[] whereArgs = { String.valueOf(memberId) }; 
        
        db.update(SimpleTraceTable.TABLE_NAME, values, whereClause, whereArgs);
    }
    
    /**
     * Append a most latest location record 
     */
    private long appendMostLatestLocationRecord(SQLiteDatabase db, 
                                                                int memberId,
                                                                long time,
                                                                double lat,
                                                                double lng){        
        ContentValues values = new ContentValues();
        values.put(SimpleTraceTable.MEMBER_LOCAL_ID, memberId);
        values.put(SimpleTraceTable.LATITUDE, lat);
        values.put(SimpleTraceTable.LONGITUDE, lng);
        values.put(SimpleTraceTable.IS_MOST_LATEST_UPDATE, 1);
        long newInsertedId = db.insert(SimpleTraceTable.TABLE_NAME, null, values);
        return newInsertedId;        
    }
    
    public long appendTraceRecordWithDb(SQLiteDatabase db, int memberId,
                                                            long time,
                                                            double lat,
                                                            double lng){        
        invalidMostLatestLocationRecord(db,memberId);
        return appendMostLatestLocationRecord(db,memberId,time,lat,lng);
    }
    
    public long appendTraceRecord(int memberGlobalId, long time, double lat, double lng){
        
        SQLiteDatabase db = this.getWritableDatabase();
        long id =this.appendTraceRecordWithDb(db, memberGlobalId,time,lat,lng);
        db.close();
        return id;
    }
    

    
    
    public static final class SimpleTraceTable implements BaseColumns{
        
        // A table that records all trace data of mine and my mates
        private SimpleTraceTable(){}
        
        
        public static final String AUTHORITY = Constants.PACKAGE_PREFIX.substring(0, 
                            Constants.PACKAGE_PREFIX.length()-1) ;
        
        /**
         * SQLite database name
         */
                
        public static final String TABLE_NAME = "SimpleTraceTable";
        
        
        public static final Uri CONTENT_URI 
                = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);
        
        
        
        
        public static final String CONTENT_TYPE
                = "vnd.android.cursor.dir/vnd.xsunset.xinterphone";

        /**
         * The content URI base for a single row of data. Callers must
         * append a numeric row id to this Uri to retrieve a row
         */
        public static final Uri CONTENT_ID_URI_BASE
                = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "/");        
        
        
        /**
         * The global ID of the member, refer to GLOBAL_ID in MatesTable
         */
        public static final String MEMBER_LOCAL_ID = "member_local_id";        
        
        public static final String LATITUDE     =   "latitude";
        
        public static final String LONGITUDE    =   "longitude";
        
        
        public static final String LAST_UPDATE_TIME = "last_update_time";
        
        /**
         * Boolean values to indicate whether this record is the most latest update location
         * 
         */
        public static final String IS_MOST_LATEST_UPDATE = "is_most_latest_update";
        
        
    
        
        
    }

}
