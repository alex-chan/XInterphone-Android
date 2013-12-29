package com.gmail.czzsunset.xinterphone;





import com.gmail.czzsunset.xinterphone.lib.USBControl;
import com.gmail.czzsunset.xinterphone.ui.MainActivity;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class FpaService extends Service  {

	private static final String TAG = "FpaService";
	private final static int ONGOING_NOTIFICATION_ID = 80080;
	
	private NotificationCompat.Builder mBuilder;
	


	//Handler, Threads
	private Handler UIHandler = new Handler();
	private USBControlServer usbConnection;	

	
    public static final String ACTION_STOP_SERVICE = "gmail.xsunset.interphonemap.action.STOP_SERVICE";
    


    private Protocol protocol;
    
    public Context self = this;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	
	@Override
	public void onCreate(){
		super.onCreate();
		
		Log.d(TAG, "onCreate");

		showNotification();		
		setupUSB();		
		
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
	
	
	
	/**
	 * Show a notification on StatusBar
	 */
	private void showNotification() {
		Log.d(TAG , "showNotification");
		
		CharSequence text = getText(R.string.notify_working);
		
		mBuilder =
				new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(text)
					.setContentText(text);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);
		
		mBuilder.setContentIntent(contentIntent);

		startForeground(ONGOING_NOTIFICATION_ID , mBuilder.build());

	}		


	

    
  




	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG,"service destroy");
		
		closeUSB();		
		//System.runFinalizersOnExit(true);
		System.exit(0);
		
		
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



	
}
