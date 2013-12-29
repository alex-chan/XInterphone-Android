package com.gmail.czzsunset.xinterphone;

import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
public class Protocol {


	
	// Protocol in fpip
	public final static int BROADCAST_LOCATION 	= 5;
	public final static int UNICAST_LOCATION 	= 6;
	public final static int BROADCAST_MESSAGE 	= 7;
	public final static int UNICAST_MESSAGE		= 8;	
	
	
	
	public final static String ACTION_UPDATE_PEER_LOCATION 
				= Constants.PACKAGE_PREFIX + "ACTION_UPDATE_PEER_LOCATION";
	public final static String ACTION_DISPLAY_RECEIVED_MESSAGE 
				= Constants.PACKAGE_PREFIX + "ACTION_DISPLAY_RECEIVED_MESSAGE";
	
	
	public final static String EXTRA_GROUP_ID 
						= Constants.PACKAGE_PREFIX + "EXTRA_GROUP_ID";
	public final static String EXTRA_USER_ID 
						= Constants.PACKAGE_PREFIX + "EXTRA_USER_ID";
	public final static String EXTRA_MILLIS 
						= Constants.PACKAGE_PREFIX + "EXTRA_MILLIS";
	
	public final static String EXTRA_LATITUDE 
						= Constants.PACKAGE_PREFIX + "EXTRA_LATITUDE";
	public final static String EXTRA_LONGITUDE 
						= Constants.PACKAGE_PREFIX + "EXTRA_LONGITUDE";
	public final static String EXTRA_ALTITUDE
						= Constants.PACKAGE_PREFIX + "EXTRA_ATLITUDE";
	public final static String EXTRA_ACCURACY 
						= Constants.PACKAGE_PREFIX + "EXTRA_ACCURACY";

	
	private final static String TAG = "Protocol";
	private Context context;
	
	private LocalBroadcastManager lbcManager;
	
	Protocol(Context context){
		this.context = context;		
		lbcManager =  LocalBroadcastManager.getInstance(context); 
	}
	
	
	
	public void processInput(byte[] msg){
		
		// Format: 2 bit version, 6 bit command,  others are command specific 

		int version =  (msg[0] >> 6);
		int command =  (msg[0] << 2 >> 2 );
		Log.d(TAG, "version:"+version+" command:"+command );
		if( version == 0){ // Currently, only version 00 implemented
			int groupId = (int)msg[1];
			int userId = (int)msg[2];
			ByteBuffer ba = ByteBuffer.wrap(msg);
			
			switch(command){
			case BROADCAST_LOCATION:
				long mills = ba.getLong(3);
				float latitude = ba.getFloat(11);
				float longitude = ba.getFloat(15);
				float altitude = ba.getFloat(19);
				float accuracy = ba.getFloat(23);
				
				
				Intent intent = new Intent(ACTION_UPDATE_PEER_LOCATION);
				intent.putExtra(EXTRA_GROUP_ID, groupId);
				intent.putExtra(EXTRA_USER_ID, userId);
				intent.putExtra(EXTRA_MILLIS, mills);
				intent.putExtra(EXTRA_LATITUDE, latitude);
				intent.putExtra(EXTRA_LONGITUDE, longitude);
				intent.putExtra(EXTRA_ALTITUDE, altitude);
				intent.putExtra(EXTRA_ACCURACY, accuracy);

				Log.d(TAG, "send intent broadcast location ");
				lbcManager.sendBroadcast(intent);
				
				
				break;
			case UNICAST_LOCATION:
				break;
			case BROADCAST_MESSAGE:
				break;
			case UNICAST_MESSAGE:
				break;
			default:
				break;
				
			}
		}
	}

	
	




}
