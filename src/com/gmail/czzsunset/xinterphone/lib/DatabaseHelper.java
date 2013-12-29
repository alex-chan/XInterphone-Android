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

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "interphone.db";
    private static final int DATABASE_VERSION = 8;
    
    private static final String TAG = "DatabaseHelper";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); 
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createTraceTable(db);
        createMatesTable(db);
        
    }
    
    private void createMatesTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + MatesTable.TABLE_NAME + " ("
                + MatesTable._ID + " INTEGER PRIMARY KEY,"
                + MatesTable.GLOBAL_ID + " INTEGER, "
                + MatesTable.LOCAL_ID + " INTEGER, "
                + MatesTable.NAME + " TEXT, "
                + MatesTable.HEAD_IMG_URL + " TEXT, "
                + MatesTable.SEX + " TEXT,"
                + MatesTable.STATUS + " INTEGER, "
                + MatesTable.UUID + " TEXT);"
                );
    }
    
    private void createTraceTable(SQLiteDatabase db){

        db.execSQL("CREATE TABLE " + TraceTable.TABLE_NAME + " ("
                + TraceTable._ID + " INTEGER PRIMARY KEY," 
                + TraceTable.MEMBER_GLOBAL_ID + " INTEGER,"                
                + TraceTable.LATITUDE   + " DOUBLE ,"
                + TraceTable.LONGITUDE + " DOUBLE,"
                + TraceTable.IS_MOST_LATEST_UPDATE + " INTEGER"
                + ");" );        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version "+ oldVersion + " to " 
                    + newVersion + ", which will destroy all old data"  );
        
        db.execSQL("DROP TABLE IF EXISTS " + TraceTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MatesTable.TABLE_NAME);
        
        onCreate(db);
    }
    
    /**
     * Set the IS_MOST_LATEST_LOCATION flag of the member ID to False(0)
     */
    private void invalidMostLatestLocationRecord(SQLiteDatabase db, int memberId){
        
        
        ContentValues values = new ContentValues();
        values.put(TraceTable.IS_MOST_LATEST_UPDATE, 0);
        
        String whereClause = TraceTable.MEMBER_GLOBAL_ID + "=?";
        String[] whereArgs = { String.valueOf(memberId) }; 
        
        db.update(TraceTable.TABLE_NAME, values, whereClause, whereArgs);
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
        values.put(TraceTable.MEMBER_GLOBAL_ID, memberId);
        values.put(TraceTable.LATITUDE, lat);
        values.put(TraceTable.LONGITUDE, lng);
        values.put(TraceTable.IS_MOST_LATEST_UPDATE, 1);
        long newInsertedId = db.insert(TraceTable.TABLE_NAME, null, values);
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
    
    public long appendMates(List<User> members){
        
        SQLiteDatabase db = this.getWritableDatabase();
        
        Log.d(TAG, "DROP MatesTable");
        db.execSQL("DROP TABLE IF EXISTS " + MatesTable.TABLE_NAME  ); 
        
        long newInsertedId = 0 ;
        
        this.createMatesTable(db);
        
        for(int i=0;i<members.size();i++){
       
            User member = members.get(i);
            
            Log.d(TAG, "Get member "+ member.getName() +",append to db");
            
            ContentValues values = new ContentValues();
            values.put(MatesTable.GLOBAL_ID, member.getId());
            values.put(MatesTable.LOCAL_ID, (int)member.getLocalId());
            values.put(MatesTable.NAME, member.getName());
            values.put(MatesTable.UUID, member.getUuid());
            values.put(MatesTable.HEAD_IMG_URL, member.getHeadImgUrl());
            values.put(MatesTable.SEX, member.getSexInString());
            values.put(MatesTable.STATUS, (member.getStatus()).ordinal());                                   
            
            newInsertedId = db.insert(MatesTable.TABLE_NAME, null, values);
            
            try{
                Thread.sleep(10);                                
            }catch(InterruptedException e){                                
            }            
        }
        db.close();
        
        return newInsertedId;
    }
    

    

    public static final class MatesTable implements BaseColumns{
        
        private MatesTable(){}
        
        public static final String AUTHORITY = Constants.PACKAGE_PREFIX.substring(0, 
                Constants.PACKAGE_PREFIX.length()-1) ;
        
        /**
         * SQLite database name
         */                
        public static final String TABLE_NAME = "MatesTable";
        
        
        public static final Uri CONTENT_URI 
                    = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);
        
        public static final String CONTENT_TYPE
                    = "vnd.android.cursor.dir/vnd.xsunset.interphonemap";        
        
        public static final String  GLOBAL_ID   = "global_id";
        public static final String  LOCAL_ID    = "local_id";
        public static final String  UUID        = "uuid";
        public static final String  NAME        = "name";
        public static final String  HEAD_IMG_URL    = "head_img_url";
        public static final String  SEX         = "sex";
        public static final String  STATUS      = "status";
        
        
        
        
    }
    
    public static final class TraceTable implements BaseColumns{
        
        // A table that records all trace data of mine and my mates
        private TraceTable(){}
        
        
        public static final String AUTHORITY = Constants.PACKAGE_PREFIX.substring(0, 
                            Constants.PACKAGE_PREFIX.length()-1) ;
        
        /**
         * SQLite database name
         */
                
        public static final String TABLE_NAME = "TraceTable";
        
        
        public static final Uri CONTENT_URI 
                = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);
        
        
        
        
        public static final String CONTENT_TYPE
                = "vnd.android.cursor.dir/vnd.xsunset.interphonemap";

        /**
         * The content URI base for a single row of data. Callers must
         * append a numeric row id to this Uri to retrieve a row
         */
        public static final Uri CONTENT_ID_URI_BASE
                = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "/");        
        
        
        /**
         * The global ID of the member, refer to GLOBAL_ID in MatesTable
         */
        public static final String MEMBER_GLOBAL_ID = "member_global_id";        
        
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
