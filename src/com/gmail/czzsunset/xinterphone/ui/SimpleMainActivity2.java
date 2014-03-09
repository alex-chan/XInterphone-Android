package com.gmail.czzsunset.xinterphone.ui;

import java.util.Date;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import com.gmail.czzsunset.xinterphone.ui.SimpleMapFragment.MarkerColor;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

public class SimpleMainActivity2 extends ActionBarActivity  implements ServiceConnection{ 

	
	

	private static final String TAG = "SimpleMainActivity2";
	
    private ILastLocationFinder mLastLocationFinder;
    private Location mLastLocation;
     
    private SharedPreferences mSharedPref ;    
    private LocationManager mLocationManager ;    
    private SimpleMapFragment mMapfrag;	
    
    
    
	private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());
	private Messenger mServiceMessenger = null;
	private ServiceConnection mConnection = this;
	private boolean mIsBound = false; 
    
    
    

    private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.i(TAG, "A location fix coming..." + location);
			if(location != null){
				LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
				
				//mMapfrag.animateToLocation(latlng, 1000);			
				
				int memberId =  mSharedPref.getInt(SimplePrefActivity.KEY_PREF_MY_CODE, 0);
						
				
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG,"onCreate");
		
		super.onCreate(savedInstanceState);
						
		setContentView(R.layout.simple_activity_main);
		
		initMembers();
		layoutUI();
		
		
		doStartService();

//		automaticBind();
						

	}	
	
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.i(TAG,"onStart");
		doBindService();
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
			Log.d(TAG, "add self marker, and animate to it");
			int userCode =  mSharedPref.getInt(SimplePrefActivity.KEY_PREF_MY_CODE, 0);
			addMarker(userCode, mLastLocation.getLatitude(),mLastLocation.getLongitude(),
					null,null, true);	
			mMapfrag.animateToLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 500);
		}
	}
		
	
	@Override
	protected void onPause(){
		super.onPause();
		Log.i(TAG,"onPause");
	}
	
	
	@Override
	protected void onStop(){
		super.onStop();
		Log.i(TAG, "onStop");
		doUnbindService();
	}
	
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		Log.i(TAG, "onDestory");
		try {	
			doUnbindService();
//			doStopService();
		} catch (Throwable t) {
			Log.e(TAG, "Failed to unbind from the service", t);
		}
	}	
	
	
	

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    
		return true;
	}
		
    @Override
    public boolean  onOptionsItemSelected(MenuItem item){
    	
    	if(item.getItemId() == R.id.action_exit){
    		// exit
    		doUnbindService();
    		doStopService();
    		this.finish();
    		return true;
    	}else if(item.getItemId() == R.id.action_settings){
    		Intent i = new Intent(this, SimplePrefActivity.class);
    		startActivity(i);
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }	
	
	
	
	private void initMembers(){

		mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		mLocationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
    	mLastLocationFinder = PlatformSpecificImplementationFactory.getLastLocationFinder(this);
    	
    	mLastLocationFinder.setChangedLocationListener(new MyLocationListener());    	
    	mLastLocation  = mLastLocationFinder.getLastBestLocation(2000, new Date().getTime() - 120 * 1000);
    	Log.d(TAG, "Got last best location:"+mLastLocation);
		
	}
	
	private void layoutUI(){
		
		
		if( mLastLocation != null){
			attachMapFragment(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())  );
		}else{
			LatLng latlng = null;
			attachMapFragment(latlng);
		}		
		
	}
	
	
	private  void attachMapFragment(LatLng latlng){
		
    	FragmentManager manager = getSupportFragmentManager();     
    	
//    	SimpleMapFragment mMapFrag = (SimpleMapFragment) manager.findFragmentByTag("tabmap");
    	
    	
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
	
	

	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/*
	 * Add a marker on Map
	 */
	public void addMarker(int userCode, double lat, double lng, String name,
			String snippet, boolean isSelf) {
//		mMapfrag.addMarker(lat, lng, userCode, null, null, isSelf);
		mMapfrag.addMarker(userCode, lat, lng, null, null,  isSelf ? MarkerColor.RED : MarkerColor.BLUE);
	}
	
	/*
	 * Move a marker to a new latlng
	 */
	public void moveMarker(int userCode, double newLat, double newLng, boolean mapWillFollow){
		if( mMapfrag != null ){
			
			mMapfrag.animateMarker(userCode,newLat,newLng,500,mapWillFollow);
		}
	}
	
	public void updateMarkerInfo(int userCode, String name, String snippet, long lastUpdateTimestamp){
		
	}

	/*
	 * Shirking a marker, indicating he is speaking... 
	 */
	public void shirkingMarker(int userCode){
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private void sendMessageToService(int what, Bundle bundle){
		if(mIsBound){
			if( mServiceMessenger != null){
				try {
					Message msg = Message.obtain(null, what);
					msg.setData(bundle);					
					msg.replyTo = mMessenger;
					mServiceMessenger.send(msg);
				} catch (RemoteException e) {
				}
			}
		} 
	}
	
	
	
	/**
	 * Bind this Activity to MyService
	 */
	private void doBindService() {
		Log.i(TAG,"bindService");
		bindService(new Intent(this, FpaService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	/**
	 * Un-bind this Activity to MyService
	 */	
	private void doUnbindService() {
		Log.d(TAG,"doUnbindService");
		if (mIsBound) {
			// If we have received the service, and hence registered with it, then now is the time to unregister.
			if (mServiceMessenger != null) {
				try {
					Log.i(TAG, "sending unbound msg to service..");
					Message msg = Message.obtain(null, FpaService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mServiceMessenger.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service has crashed.
				}
			}
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}


	private void doStartService(){
		
		Log.i(TAG,"startService");
		startService(new Intent(SimpleMainActivity2.this, FpaService.class));
	}
	
	private void doStopService(){
		Log.i(TAG,"stopService");
		stopService(new Intent(SimpleMainActivity2.this, FpaService.class));
	}	

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.i(TAG,"onServiceConnected");
		mServiceMessenger = new Messenger(service);
		try {
			Message msg = Message.obtain(null, FpaService.MSG_REGISTER_CLIENT);
			msg.replyTo = mMessenger;
			mServiceMessenger.send(msg);
		} 
		catch (RemoteException e) {
			// In this case the service has crashed before we could even do anything with it
		} 
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// This is called when the connection with the service has been unexpectedly disconnected - process crashed.
		Log.i(TAG,"onServiceDisconnected");
		mServiceMessenger = null;
	}



	/**
	 * Handle incoming messages from FpaService
	 */
	private class IncomingMessageHandler extends Handler {		
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG,"IncomingHandler:handleMessage");
			switch (msg.what) {
			case FpaService.MSG_DRAW_MARKER:
				
				// addMarker() called here
				break;
				
			case FpaService.MSG_UPDATE_MARKER:
				
				// updateMarker() called here
				
				Bundle bundle = (Bundle) msg.getData();
				
				int userCode = bundle.getInt("userCode");
				double newLat = bundle.getDouble("lat");
				double newLng = bundle.getDouble("lng");
				double timestamp = bundle.getDouble("timestamp");
				
				int myCode =  mSharedPref.getInt(SimplePrefActivity.KEY_PREF_MY_CODE, 0);
				
				
				moveMarker(userCode,newLat,newLng, myCode == userCode);
				
				break;
			
			default:
				super.handleMessage(msg);
			}
		}
	}		

	

}
