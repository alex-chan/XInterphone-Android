package com.gmail.czzsunset.xinterphone;





import java.nio.ByteBuffer;
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
import com.gmail.czzsunset.xinterphone.model.SimpleUser;
import com.gmail.czzsunset.xinterphone.ui.MainActivity;
import com.gmail.czzsunset.xinterphone.ui.PeerManager;
import com.gmail.czzsunset.xinterphone.ui.SimpleMainActivity;
import com.gmail.czzsunset.xinterphone.ui.SimpleMainActivity2;
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
	public static final int MSG_DRAW_MARKER_LIST  = 0xe000004;
	
	public static final int MSG_EXIT_APP          = 0xeffffff;
	
	
	
	
	private final Messenger mMessenger = new Messenger(new IncomingMessageHandler()); // Target we publish for clients to send messages to IncomingHandler
	
	
	private static boolean isRunning = false;
	private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.	
	
	
	
	private Protocol protocol;    
    public static FpaService self = null;
    
    private static PeerManager mPeerManager = new PeerManager() ;
    
    
    private LocationUpdateRequester mLocationRequester;
    static SharedPreferences mSharedPref ;
    PendingIntent reqLocUpdatePendingIntent;
    
    private long lastBroadcastMs = 0;
    
    public static LocationListener locListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			Log.d(TAG,"onLocationChanged");
			if(location != null ){
    			
    			double lat = location.getLatitude();
    			double lng = location.getLongitude();
    			Log.d(TAG, "locListener received a new location fix, lat:"+lat+" lng:"+lng );
    			
    			String sUserCode = mSharedPref.getString(SimplePrefActivity.KEY_PREF_MY_CODE, "0") ;
    			
    			int interMin = Integer.valueOf(  mSharedPref.getString(SimplePrefActivity.KEY_PREF_UPDATE_INTERVAL, "5") );
    			
    			
    			int userCode = Integer.valueOf( sUserCode );
    			// int userCode =  mSharedPref.getInt(SimplePrefActivity.KEY_PREF_MY_CODE, 0);
    			int iUUID = Util.getIUUID(self, 0);
    			
    			mPeerManager.updateMySelf(iUUID,userCode,lat,lng,0.0,location.getTime());
    			
    			Bundle bundle = mPeerManager.getBundle(iUUID);
    			
    			self.sendMessageToUI(MSG_UPDATE_MARKER, bundle);    	
    			
    			long curMs =  System.currentTimeMillis();
    			if( (curMs - self.lastBroadcastMs) > interMin * 60 * 1000 ){
    				self.lastBroadcastMs = curMs;
    			}
    			
    			
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
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
    	
    };
    
    
    public static class LocUpdateFromHost extends BroadcastReceiver{
    	
 
    	
    	@Override
		public void onReceive(Context context, Intent intent){
    		
    		Bundle bd = intent.getExtras();
    		Location location = (Location)bd.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
    		
    		Log.d(TAG,"reveiving a location fix...");
    		
    		if(location != null ){
    			
    			double lat = location.getLatitude();
    			double lng = location.getLongitude();
    			Log.d(TAG, "LocUpdateFromHost received a new location fix, lat:"+lat+" lng:"+lng );
    			
    			String sUserCode = mSharedPref.getString(SimplePrefActivity.KEY_PREF_MY_CODE, "0") ;
    			
    			int interMin = Integer.valueOf(  mSharedPref.getString(SimplePrefActivity.KEY_PREF_UPDATE_INTERVAL, "5") );
    			
    			
    			int userCode = Integer.valueOf( sUserCode );
    			// int userCode =  mSharedPref.getInt(SimplePrefActivity.KEY_PREF_MY_CODE, 0);
    			int iUUID = Util.getIUUID(self, 0);
    			
    			mPeerManager.updateMySelf(iUUID,userCode,lat,lng,0.0,location.getTime());
    			
    			Bundle bundle = mPeerManager.getBundle(iUUID);
    			
    			self.sendMessageToUI(MSG_UPDATE_MARKER, bundle);    	
    			
    			long curMs =  System.currentTimeMillis();
    			if( (curMs - self.lastBroadcastMs) > interMin * 60 * 1000 ){
    				self.lastBroadcastMs = curMs;
    			}
    			
    			
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

		initMembers();
		
		setupUSB();		
		testUSB_v1();
		
		requestLocationUpdate2(4 * 1000);
		
		self = this;
		
		
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i(TAG,"service destroy");
		isRunning = false;
		
		closeUSB();		
		
		
		cancelLocationUpdate2();

		System.exit(0);
		
		
	}
	
	
	private void initMembers(){
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);	
		mLocationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
		mLocationRequester = PlatformSpecificImplementationFactory.getLocationUpdateRequester(mLocationManager);		
		
		protocol = new Protocol();
		
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
	
	
	private byte[] unittest(int sleepSec, int iUUID, int userCode, double lat, double lng){
		try {
			Thread.sleep( sleepSec * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte []msg = new byte[28];
		
		ByteBuffer bb = ByteBuffer.allocate(28);
		bb.put((byte) 0x05);
		bb.put((byte) 0x00);
		bb.put((byte) userCode); // 				
		bb.put((byte) iUUID); // iUUID = 32
		
		bb.putLong( System.currentTimeMillis());
		bb.putFloat((float) lat);  // latitude
		bb.putFloat((float) lng);  // longitude
		bb.putFloat((float) 110);  // altitude
		bb.putFloat((float) 20);  // accuracy 
		
		msg = bb.array();	
		return msg;
	}
	
	private void testUSB_v1(){
		
		Thread thd = new Thread(new Runnable(){
			public void run() {
				
				Log.d(TAG, "receiving msg from peer...");
			
				
				usbConnection.onReceive(unittest(5,0x30,1,22.586,113.955));
				usbConnection.onReceive(unittest(5,0x31,2,22.584,113.952));
				usbConnection.onReceive(unittest(5,0x30,1,22.583,113.951));
				
				
				
				
			}				
		});
		thd.start();
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void requestLocationUpdate(){
		int interInMin = Integer.valueOf( mSharedPref.getString(SimplePrefActivity.KEY_PREF_UPDATE_INTERVAL, "5") );		
		requestLocationUpdate(interInMin * 60 * 1000);
	}
	
    private void requestLocationUpdate(long intervalMs){
    	
    	Log.d(TAG, "requestLocationUpdate, intervalMs:" + intervalMs);
    	    	    	
    	
    	    	
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	
    	    	
    	Intent intent = new Intent(this, LocUpdateFromHost.class);    	    	
    	reqLocUpdatePendingIntent = PendingIntent.getBroadcast(this,
    														0, intent, PendingIntent.FLAG_UPDATE_CURRENT);    	   
    	
    	mLocationRequester.requestLocationUpdates(intervalMs, 0, criteria, reqLocUpdatePendingIntent);
    	
    }
    private void cancelLocationUpdate(){
    	if(reqLocUpdatePendingIntent != null){
    		Log.d(TAG,"cancel request");
    		reqLocUpdatePendingIntent.cancel();
    		reqLocUpdatePendingIntent = null;    		
    	}
    		
    }
	

    
    
    
    
    private void requestLocationUpdate2(){ 	
		int interInMin = Integer.valueOf( mSharedPref.getString(SimplePrefActivity.KEY_PREF_UPDATE_INTERVAL, "5") );		
		requestLocationUpdate2(interInMin * 60 * 1000);    	
    }
    
    private void requestLocationUpdate2(long intervalMs){
    	Log.d(TAG, "requestLocationUpdate, intervalMs:" + intervalMs);
    	
    	mLocationRequester = PlatformSpecificImplementationFactory.getLocationUpdateRequester(mLocationManager);
    	    	
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);    	
    	    	  	
    	// mLocationRequester.requestLocationUpdates(intervalMs, 0, criteria, reqLocUpdatePendingIntent);
    	mLocationRequester.requestLocationUpdates(intervalMs, 0, criteria, locListener, getMainLooper());
    	
    	
    }
    
    private void cancelLocationUpdate2(){
    	mLocationRequester.removeUpdate(locListener);
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
				new Intent(this, SimpleMainActivity2.class), PendingIntent.FLAG_UPDATE_CURRENT);
		
		mBuilder.setContentIntent(contentIntent);

		startForeground(ONGOING_NOTIFICATION_ID , mBuilder.build());

	}		



	

	
	public  void sendMessageToUI(int what, Bundle bundle) {
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
				
				
//				cancelLocationUpdate2();
//				requestLocationUpdate2(4000);
				
				
				self.sendMessageToUI(MSG_DRAW_MARKER_LIST, mPeerManager.getAllPeerBundle() ); 
				
				
				break;
				
			case MSG_UNREGISTER_CLIENT:
				
//				cancelLocationUpdate2();
//				requestLocationUpdate2();
				
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
			Log.d(TAG,"onReceive:"+msg.toString());
			protocol.processInput(msg);		
			
			SimpleUser peer = protocol.getPeer();
			if( peer != null){
				
				boolean updatePeer =  mPeerManager.updatePeer( peer ) ;
				
				if( updatePeer){				
					self.sendMessageToUI(MSG_UPDATE_MARKER, mPeerManager.getBundle(peer.iUUID));					
				}else{
					self.sendMessageToUI(MSG_DRAW_MARKER, mPeerManager.getBundle(peer.iUUID));
				}
				
				protocol.clear();
				
			}
			
			
//			if(protocol.what != -1){
//				self.sendMessageToUI(protocol.getMsgType(), protocol.getBundle());
//				protocol.clear();
//			}
			
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
		
		public void broadcastMyLocation(int version, SimpleUser peer){
			byte[] msg = protocol.processOutput(0, Protocol.BROADCAST_LOCATION, peer);
			usbConnection.send(msg);
			
		}

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
