package com.gmail.czzsunset.xinterphone.ui;

import java.util.Date;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.gmail.czzsunset.xinterphone.FpaService;
import com.gmail.czzsunset.xinterphone.Protocol;
import com.gmail.czzsunset.xinterphone.R;
import com.gmail.czzsunset.xinterphone.lib.SimpleDatabaseHelper;
import com.gmail.czzsunset.xinterphone.lib.SimpleDatabaseHelper.SimpleTraceTable;
import com.gmail.czzsunset.xinterphone.locations.PlatformSpecificImplementationFactory;
import com.gmail.czzsunset.xinterphone.locations.base.ILastLocationFinder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

public class SimpleMainActivity extends ActionBarActivity  implements LoaderManager.LoaderCallbacks<Cursor> { 

	
	

	private static final String TAG = "SimpleMainActivity";

	
    

	LocalBroadcastManager mLbcManager ;
	

	
    private ILastLocationFinder mLastLocationFinder;
    private Location mLastLocation;
    
    private SimpleDatabaseHelper mDbHelper;
    
    SharedPreferences mSharedPref ;
    
    private LocationManager mLocationManager ;
    
    private SimpleMapFragment mMapfrag;
    
 

    private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.i(TAG, "A location fix coming..." + location);
			if(location != null){
				LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
				
				mMapfrag.animateToLocation(latlng, 1000);			
				
				int memberId =  mSharedPref.getInt(SimplePrefActivity.KEY_PREF_MY_CODE, 0);
						
				mDbHelper.appendTraceRecord(memberId, location.getTime(), location.getLatitude(),
																			location.getLongitude());	
			}
				
				
			
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
			
			Log.d(TAG, "protocolReceiver receieved action:"+action);
			
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
	
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				
				break;
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG,"onCreate");
		
		super.onCreate(savedInstanceState);
		
		

		
		
		setContentView(R.layout.simple_activity_main);

		
				
		setupActionBar();
		
		mDbHelper = new SimpleDatabaseHelper(this);
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		mLocationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
		
		
		setupMapFragment();
		


		
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
    	FpaService.SetHandler(mHandler);
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
//    		cancelRequestLocationUpdate();
    		this.finish();
    		return true;
    	}else if(item.getItemId() == R.id.action_settings){
    		Intent i = new Intent(this, SimplePrefActivity.class);
    		startActivity(i);
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }

    
    
    private void setupMapFragment(){
    	
    	mLastLocationFinder = PlatformSpecificImplementationFactory.getLastLocationFinder(this);		
    	
    	mLastLocationFinder.setChangedLocationListener(new MyLocationListener());
    	
    	mLastLocation  = mLastLocationFinder.getLastBestLocation(2000, new Date().getTime() - 120 * 1000);
		
		Log.d(TAG, "Got last best location:"+mLastLocation);
		if( mLastLocation != null){
			attachMapFragment(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())  );
		}else{
			LatLng latlng = null;
			attachMapFragment(latlng);
		}
    	
    }
    

        
    
	public void attachMapFragment(LatLng latlng){
		
    	FragmentManager manager = getSupportFragmentManager();     
    	
 
    	if( latlng !=null ){
    		mMapfrag = SimpleMapFragment.newInstance(latlng);
    	}else{
    		mMapfrag = SimpleMapFragment.newInstance();
    	}
    	
		manager.beginTransaction()
			.add(R.id.host, mMapfrag, "tabmap")
			.commit();    	
    	

//			    mCommMapFragment.setRetainInstance(true);
		
	}
	
	public void addMarker(double lat, double lng, int indexInGroup, String name,
			String snippet, boolean isSelf) {
		
		if(mMapfrag != null){
			//mMapfrag.addMarker(lat,lng,indexInGroup,name,snippet,isSelf);
		}
	}

    
	@Override
	public void onResume(){
		Log.d(TAG, "onResume");
		
		super.onResume();
		
		int ret = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if( ret !=  ConnectionResult.SUCCESS){
			if (GooglePlayServicesUtil.isUserRecoverableError(ret)) {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(ret, this,66);
				dialog.show();
				
		    } else {
		      Toast.makeText(this, "This device is not supported.", 
		          Toast.LENGTH_LONG).show();
		      finish();
		    }						
			
			return ;
		}
		
		
		if( mLastLocation !=null ){
			addMarker(mLastLocation.getLatitude(),mLastLocation.getLongitude(),
					0,null,null, false);	
		}
		getSupportLoaderManager().initLoader(0, null,   this);		
				
	}
	
	

	@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] MEMBERS_PROJECTION = new String[] {
        	SimpleTraceTable._ID,
        	SimpleTraceTable.MEMBER_LOCAL_ID,
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
        Log.i(TAG, "onLoadFinished." + "get "+data.getCount() + " records");
        int myCode = mSharedPref.getInt(SimplePrefActivity.KEY_PREF_MY_CODE, 0);
        
        Log.d(TAG,"myCode:" + myCode);
        
    	data.moveToFirst();
    	while(!data.isAfterLast()){
//    		Log.d(TAG, "Cursor get data");
    		
    		int indexInGroup = data.getInt(
    				data.getColumnIndex(SimpleTraceTable.MEMBER_LOCAL_ID));
    		double lat = data.getDouble(
    				data.getColumnIndex( SimpleTraceTable.LATITUDE ));
    		double lng  = data.getDouble(
    				data.getColumnIndex(SimpleTraceTable.LONGITUDE ));
//    		Log.d(TAG, "member " + indexInGroup + " at lat:"+lat+" lng:"+lng);
    		
    		boolean isSelf = false;    		
    		
    		if( indexInGroup == myCode){
    			Log.i(TAG, "Is myself...");
    			isSelf = true;    			
    		}
    		addMarker(lat,lng,indexInGroup,null,null, isSelf);
    		
    		data.moveToNext();
    	}
    	
    }		
    
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}			



}

