package com.gmail.czzsunset.xinterphone.ui;

import java.util.Date;

import com.gmail.czzsunset.xinterphone.FpaService;
import com.gmail.czzsunset.xinterphone.Protocol;
import com.gmail.czzsunset.xinterphone.R;
import com.gmail.czzsunset.xinterphone.lib.DatabaseHelper.MatesTable;
import com.gmail.czzsunset.xinterphone.lib.DatabaseHelper.TraceTable;
import com.gmail.czzsunset.xinterphone.lib.SimpleDatabaseHelper.SimpleTraceTable;
import com.gmail.czzsunset.xinterphone.locations.PlatformSpecificImplementationFactory;
import com.gmail.czzsunset.xinterphone.locations.base.ILastLocationFinder;

import com.google.android.gms.maps.model.LatLng;

import android.annotation.TargetApi;
import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class SimpleMainActivity extends ActionBarActivity  implements LoaderManager.LoaderCallbacks<Cursor> { 

	
	

	private static final String TAG = "SimpleMainActivity";

	
    

	LocalBroadcastManager mLbcManager ;
	
	BaseMapFragment mBaseMap ;
	
    private ILastLocationFinder mLastLocationFinder;

    private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.d(TAG, "A location fix coming...");
			mBaseMap.animateToLocation(location, 1000);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
			
		}
    	
    };

	
	private BroadcastReceiver protocolReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			Log.d(TAG, "receieved action"+action);
			
			if( Protocol.ACTION_UPDATE_PEER_LOCATION.equals(action)){
				// Update the location of peer 
				//Fragment fragment = getActionBar().get(0); // MapFragment is at index 0
				
				
		    	FragmentManager manager = getSupportFragmentManager();  
		    	Fragment fragment = manager.findFragmentByTag("TAG_MY_MAP");
		    	

				int groupId = intent.getIntExtra(Protocol.EXTRA_GROUP_ID, 0);
				int userId = intent.getIntExtra(Protocol.EXTRA_USER_ID, 0);
				long mills = intent.getLongExtra(Protocol.EXTRA_MILLIS, 0L);
				
				float lat = intent.getFloatExtra(Protocol.EXTRA_LATITUDE, 0f);
				float lng = intent.getFloatExtra(Protocol.EXTRA_LONGITUDE, 0f);
				float alt = intent.getFloatExtra(Protocol.EXTRA_ALTITUDE, 0f);
				float acc = intent.getFloatExtra(Protocol.EXTRA_ACCURACY, 0f);
				
				LatLng latlng = new LatLng(lat, lng);
				
//				fragment.addMarker(latlng, 1, userId, "UNDEFINED", "SNIP"	);
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_activity_main);

		mBaseMap = attachBaseMapFragment();

		setupActionBar();

		registerServiceUpdate();
		
				
		startService();
		

	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy(){
		mLbcManager.unregisterReceiver(protocolReceiver);
		super.onDestroy();

		System.exit(1);
		
	}
    /**
     * Backward-compatible version of {@link ActionBar#getThemedContext()} that
     * simply returns the {@link android.app.Activity} if
     * <code>getThemedContext</code> is unavailable.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public Context getActionBarThemedContextCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return getSupportActionBar().getThemedContext();
        } else {
            return this;
        }
    }	

	private void registerServiceUpdate(){
        IntentFilter  filter = new IntentFilter(Protocol.ACTION_UPDATE_PEER_LOCATION);
        
        mLbcManager = LocalBroadcastManager.getInstance(this);
        mLbcManager.registerReceiver(protocolReceiver , filter);	    
	}
	
	
	/** Setup ActionBar and Spinner
	 */
	private void setupActionBar(){
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
	}


    
    

    

	
	
    public void startService(){
    	Log.d(TAG,"startService");
    	startService(new Intent(this,
                FpaService.class));    	
    }	

    public void stopService(){
    	Log.d(TAG, "stopService");
    	stopService(new Intent(this, FpaService.class));
    }
   
    
    @Override
    public boolean  onOptionsItemSelected(MenuItem item){
    	
    	if(item.getItemId() == R.id.action_exit){
    		// exit
//    		Intent exitIntent = new Intent( FpaService.ACTION_STOP_SERVICE );
//    		sendBroadcast(exitIntent);
    		stopService();
    		this.finish();
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }

    
    
	/**
	 * Attach the CommonMapFragment instance to current fragment if needed
	 * @return 
	 */
	public BaseMapFragment attachBaseMapFragment(){
		
	
		
    	FragmentManager manager = getSupportFragmentManager();      	
    	BaseMapFragment map =  BaseMapFragment.newInstance();
		manager.beginTransaction()
			.add(R.id.host, map, "tabmap")
			.commit();    	
		

//			    mCommMapFragment.setRetainInstance(true);
		    

		map.setUpMapIfNeeded();
		return map;
		
	}

    
	@Override
	public void onResume(){
		Log.d(TAG, "onResume");
		
		super.onResume();
		
		mBaseMap.setUpMapIfNeeded();		
		
		
		mLastLocationFinder = PlatformSpecificImplementationFactory.getLastLocationFinder(this);
		
		mLastLocationFinder.setChangedLocationListener(new MyLocationListener());
		
		Date dt = new Date();
		Log.d(TAG, ""+ dt.getTime());
		Location loc = mLastLocationFinder.getLastBestLocation(2000, dt.getTime() - 120 * 1000);
		mBaseMap.animateToLocation(loc, 0);
		
		getSupportLoaderManager().initLoader(0, null,  this);
				
	}
	
	@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] MEMBERS_PROJECTION = new String[] {
        	SimpleTraceTable._ID,
            SimpleTraceTable.LATITUDE,
            SimpleTraceTable.LONGITUDE
        };
        
        return new CursorLoader(this, 
                Uri.withAppendedPath(SimpleTraceTable.CONTENT_URI, "latest"),
                MEMBERS_PROJECTION,
                null,
                null,
                null);        

    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
//        mAdapter.swapCursor(data);
        Log.d(TAG, "onLoadFinished." + "get "+data.getCount() + " records");
        
    	data.moveToFirst();
    	while(!data.isAfterLast()){
    		Log.d(TAG, "Cursor get data");
    		
    		double lat = data.getDouble(
    				data.getColumnIndex( SimpleTraceTable.LATITUDE ));
    		double lng  = data.getDouble(
    				data.getColumnIndex(SimpleTraceTable.LONGITUDE ));
    		Log.d(TAG, "lat:"+lat+" lng:"+lng);
    		
    		data.moveToNext();
    	}
    	
    }		
    
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}	


}

