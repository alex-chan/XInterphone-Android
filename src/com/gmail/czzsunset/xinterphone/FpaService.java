package com.gmail.czzsunset.xinterphone;





import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.gmail.czzsunset.xinterphone.lib.SimpleDatabaseHelper;
import com.gmail.czzsunset.xinterphone.lib.USBControl;
import com.gmail.czzsunset.xinterphone.locations.PlatformSpecificImplementationFactory;
import com.gmail.czzsunset.xinterphone.locations.base.LocationUpdateRequester;
import com.gmail.czzsunset.xinterphone.ui.MainActivity;
import com.gmail.czzsunset.xinterphone.ui.SimpleMainActivity;
import com.gmail.czzsunset.xinterphone.ui.SimpleMapFragment;
import com.gmail.czzsunset.xinterphone.ui.SimplePrefActivity;
import com.google.android.gms.maps.model.LatLng;

public class FpaService extends Service implements LocationListener  {

	private static final String TAG = "FpaService";
	private final static int ONGOING_NOTIFICATION_ID = 80080;
	
	private NotificationCompat.Builder mBuilder;
	
    private LocationManager mLocationManager ;

	//Handler, Threads
	private Handler UIHandler = new Handler();
	private USBControlServer usbConnection;	

	
    
    public static final int MSG_UNREGISTER_CLIENT = 0xe000000;
	public static final int MSG_REGISTER_CLIENT   = 0xe000001;
	public static final int MSG_DRAW_MARKER 	  = 0xe000002;
	public static final int MSG_UPDATE_MARKER	  = 0xe000003;
	public static final int MSG_EXIT_APP   = 0xe000004;
	
	
	private final Messenger mMessenger = new Messenger(new IncomingMessageHandler()); // Target we publish for clients to send messages to IncomingHandler
	
	
	
	private static boolean isRunning = false;
	private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.	
	
	
	
	private Protocol protocol;    
    public static FpaService self = null;
    
    
    private LocationUpdateRequester mLocationRequester;
    static SharedPreferences mSharedPref ;
    PendingIntent reqLocUpdatePendingIntent;
    
    
    
    
    
    private static SimpleDatabaseHelper mDbHelper;   
    private static Handler mHandler;
    
    public static void SetHandler(Handler handler){
    	mHandler = handler;
    }
    
    public static class LocUpdateFromHost extends BroadcastReceiver{
    	
 
    	
    	@Override
		public void onReceive(Context context, Intent intent){
    		
    		Bundle bd = intent.getExtras();
    		Location location = (Location)bd.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
    		
    		if(location != null ){
    			
    			double lat = location.getLatitude();
    			double lng = location.getLongitude();
    			Log.d(TAG, "LocUpdateFromHost received a new location fix, lat:"+lat+" lng:"+lng );
    			
    			int userCode =  mSharedPref.getInt(SimplePrefActivity.KEY_PREF_MY_CODE, 0);
    			
    			Bundle bundle = new Bundle();
    			bundle.putInt("userCode", userCode);
    			bundle.putDouble("lat", lat);
    			bundle.putDouble("lng", lng);
    			bundle.putDouble("timestamp", location.getTime());
    			self.sendMessageToUI(MSG_UPDATE_MARKER, bundle);    			
    			
    		}

			
//			
//			mDbHelper.appendTraceRecord(memberId, location.getTime(), location.getLatitude(),
//																location.getLongitude());	
			
			
		}
    }
    
//    public LocUpdateFromPhoneBroadcastReceiver locUpdate = new LocUpdateFromPhoneBroadcastReceiver();
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.i(TAG, "onBind");
		return mMessenger.getBinder();
	}

	
	
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		Log.d(TAG, "onCreate");

		showNotification();		
//		setupUSB();		
		initMembers();
		
		requestLocationUpdate(1 * 60 * 1000);
		
		self = this;
		
		
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i(TAG,"service destroy");
		isRunning = false;
		
//		closeUSB();		
		
		
		cancelLocationUpdate();

		System.exit(0);
		
		
	}
	
	
	private void initMembers(){
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);	
		mLocationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
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
		int interInMin = Integer.parseInt( mSharedPref.getString(SimplePrefActivity.KEY_PREF_UPDATE_INTERVAL, "5") );		
		requestLocationUpdate(1 * 60 * 1000);
	}
	
    private void requestLocationUpdate(long intervalMs){
    	
    	Log.d(TAG, "requestLocationUpdate, intervalMs:" + intervalMs);
    	    	    	
    	mLocationRequester = PlatformSpecificImplementationFactory.getLocationUpdateRequester(mLocationManager);
    	    	
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_LOW);
    	    	
    	Intent intent = new Intent(this, LocUpdateFromHost.class);    	    	
    	reqLocUpdatePendingIntent = PendingIntent.getBroadcast(this,
    														0, intent, PendingIntent.FLAG_UPDATE_CURRENT);    	   
    	
    	mLocationRequester.requestLocationUpdates(intervalMs, 0, criteria, reqLocUpdatePendingIntent);
    	
    }
    private void cancelLocationUpdate(){
    	if(reqLocUpdatePendingIntent != null){
    		reqLocUpdatePendingIntent.cancel();
    	}
    		
    }
	

	
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
				new Intent(this, SimpleMainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		
		mBuilder.setContentIntent(contentIntent);

		startForeground(ONGOING_NOTIFICATION_ID , mBuilder.build());

	}		



	

	
	private  void sendMessageToUI(int what, Bundle bundle) {
		Iterator<Messenger> messengerIterator = mClients.iterator();		
		while(messengerIterator.hasNext()) {
			Messenger messenger = messengerIterator.next();
			try {
				// Send data as an Integer				
				// messenger.send(Message.obtain(null, MSG_SET_INT_VALUE, intvaluetosend, 0));
				// Send data as a String
				//Bundle bundle = new Bundle();
				//bundle.putString("str1", "ab" + intvaluetosend + "cd");
				//bundle.putDouble("lat", value)
				Message msg = Message.obtain(null, what);
				msg.setData(bundle);
				messenger.send(msg);

			} catch (RemoteException e) {
				// The client is dead. Remove it from the list.
				mClients.remove(messenger);
			}
		}
	}

	public static boolean isRunning()
	{
		return isRunning;
	}



	/**
	 * Handle incoming messages from MainActivity
	 */
	private class IncomingMessageHandler extends Handler { // Handler of incoming messages from clients.
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG,"handleMessage: " + msg.what);
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				
				cancelLocationUpdate();
				requestLocationUpdate(2000);
				
				break;
				
			case MSG_UNREGISTER_CLIENT:
				
				cancelLocationUpdate();
				requestLocationUpdate();
				
				mClients.remove(msg.replyTo);
				
				break;
			case MSG_EXIT_APP :
				// 
				break;
			default:
				super.handleMessage(msg);
			}
		}
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
