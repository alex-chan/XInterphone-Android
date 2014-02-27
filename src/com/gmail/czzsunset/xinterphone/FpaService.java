package com.gmail.czzsunset.xinterphone;





import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.gmail.czzsunset.xinterphone.lib.SimpleDatabaseHelper;
import com.gmail.czzsunset.xinterphone.lib.USBControl;
import com.gmail.czzsunset.xinterphone.locations.PlatformSpecificImplementationFactory;
import com.gmail.czzsunset.xinterphone.locations.base.LocationUpdateRequester;
import com.gmail.czzsunset.xinterphone.ui.MainActivity;
import com.gmail.czzsunset.xinterphone.ui.SimplePrefActivity;

public class FpaService extends Service implements LocationListener  {

	private static final String TAG = "FpaService";
	private final static int ONGOING_NOTIFICATION_ID = 80080;
	
	private NotificationCompat.Builder mBuilder;
	
    private LocationManager mLocationManager ;

	//Handler, Threads
	private Handler UIHandler = new Handler();
	private USBControlServer usbConnection;	

	
    public static final String ACTION_STOP_SERVICE =
    		 	Constants.PACKAGE_PREFIX + "action.STOP_SERVICE";
    
    public static final String ACTION_HOST_LOC_UPDATE_RECEIVED =     		
   		 		Constants.PACKAGE_PREFIX +  "action.HOST_LOC_UPDATE_RECEIVED";


    private Protocol protocol;
    
    public Context self = this;
    
    
    private LocationUpdateRequester mLocationRequester;
    SharedPreferences mSharedPref ;
    PendingIntent reqLocUpdatePendingIntent;
    
    
    public static class LocUpdateFromPhoneBroadcastReceiver extends BroadcastReceiver{
    	
 
    	
    	@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			Log.d(TAG, "LocUpdateFromPhoneBroadcastReceiver received action" + action);
			
			
		}
    }
    
//    public LocUpdateFromPhoneBroadcastReceiver locUpdate = new LocUpdateFromPhoneBroadcastReceiver();
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	
	@Override
	public void onCreate(){
		super.onCreate();
		
		Log.d(TAG, "onCreate");

		showNotification();		
//		setupUSB();		
		
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);	
		mLocationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
		
		
//		registerLocationUpdateBroadcastReceiver();
		
		requestLocationUpdate();
		
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i(TAG,"service destroy");
		
//		closeUSB();		
		
		
		cancelRequestLocationUpdate();
//		unregisterLocationUpdateBroadcastReceiver();
		
//		mLocationManager.removeUpdates(this);
		
		//System.runFinalizersOnExit(true);
		System.exit(0);
		
		
	}
	
	
	
	private void setupUSB(){

		console("Starting USB...");
		Log.i(TAG, "Starting USB2...");
		usbConnection = new USBControlServer(UIHandler);
		console("Done\n");
		Log.i(TAG, "Done2\n");
	}
	
	private void closeUSB(){
		console("Closing USB...");
		usbConnection.closeAccessory();
		usbConnection.destroyReceiver();
		console("Done\n");	
	}
	
	
	
    private void requestLocationUpdate(){
    	
    	Log.d(TAG, "requestLocationUpdate");
    	
    	
    	
    	mLocationRequester = PlatformSpecificImplementationFactory.getLocationUpdateRequester(mLocationManager);
    	
    	int interInMin = Integer.parseInt( mSharedPref.getString(SimplePrefActivity.KEY_PREF_UPDATE_INTERVAL, "5") );
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_LOW);
    	
    	
//    	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 0, this);
    	
//    	return;
    	
    	
    	
//    	Intent intent = new Intent(ACTION_HOST_LOC_UPDATE_RECEIVED );
    	Intent intent = new Intent(this, LocUpdateFromPhoneBroadcastReceiver.class);
    	
    	
    	reqLocUpdatePendingIntent = PendingIntent.getBroadcast(this,
    														0, intent, PendingIntent.FLAG_UPDATE_CURRENT);    	
    	
    	Log.d(TAG, "reqLocUpdatePendingIntent" +  reqLocUpdatePendingIntent);
    	
    	mLocationRequester.requestLocationUpdates(interInMin * 60, 0, criteria, reqLocUpdatePendingIntent);

    	
//    	mLocationManager.requestLocationUpdates(20 * 1000, 0, criteria, reqLocUpdatePendingIntent);
//    	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 0, reqLocUpdatePendingIntent);    	
//    	mLocationRequester.requestLocationUpdates(20 * 1000, 0 , criteria, reqLocUpdatePendingIntent);     	    	
    	
    	
    }
    private void cancelRequestLocationUpdate(){
    	if(reqLocUpdatePendingIntent != null){
    		reqLocUpdatePendingIntent.cancel();
    	}
    		
    }
	
//	public void registerLocationUpdateBroadcastReceiver(){
//		
//		Log.i(TAG, "register location update broadcast receiver");
//
//		IntentFilter intentFilter = new IntentFilter();
//		
//		registerReceiver(locUpdate,intentFilter);
//		
//	}
//	public void unregisterLocationUpdateBroadcastReceiver(){
//		unregisterReceiver(locUpdate);
//	}
	
	/**
	 * Show a notification on StatusBar
	 */
	private void showNotification() {
		Log.i(TAG, "showNotification");
		
		CharSequence text = getText(R.string.notify_working);
		
		mBuilder =
				new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(text)
					.setContentText(text);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		
		mBuilder.setContentIntent(contentIntent);

		startForeground(ONGOING_NOTIFICATION_ID , mBuilder.build());

	}		






	
	public class USBControlServer extends USBControl{


		
		public USBControlServer(Handler ui) {
			super(getApplicationContext(), ui);
		}

		@Override
		public void onReceive(byte[] msg) {			
			protocol = new Protocol(self);
			protocol.processInput(msg);					
			
		}

		@Override
		public void onNotify(String msg) {
			console(msg);
		}

		@Override
		public void onConnected() {

		}

		@Override
		public void onDisconnected() {
			
			//finish();
		}

		byte[] msg = new byte[3];

		void setSpeed(int speed){

			usbConnection.send(msg);
		}
	}	
	

	//Helper
	public void toast (final Object msg){
		UIHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg.toString(), Toast.LENGTH_SHORT).show();	
				Log.i(TAG, msg.toString());
			}
		});
	}

	public void console (final Object msg){
		UIHandler.post(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, msg.toString());
//				console.append(msg.toString());
//				console_scroll.fullScroll(View.FOCUS_DOWN);
			}
		});
	}


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		Log.i(TAG,"FpaService location changed:"+location);
		
		if( location != null){
			long time = location.getTime();
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);		
			int memberId =  sp.getInt(SimplePrefActivity.KEY_PREF_MY_CODE, 0);
			SimpleDatabaseHelper dbHelper = new SimpleDatabaseHelper(this);
			dbHelper.appendTraceRecord(memberId, time, lat, lng);			
	
		}
				
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}



	
}
