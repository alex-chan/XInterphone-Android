package com.gmail.czzsunset.xinterphone;

import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
public class Protocol {


	
	// Protocol in fpip
	public final static int BROADCAST_LOCATION 	= 0x05;
	public final static int UNICAST_LOCATION 	= 0x06;
	public final static int BROADCAST_MESSAGE 	= 0x07;
	public final static int UNICAST_MESSAGE		= 0x08;	
	
	
	
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
	
//	private LocalBroadcastManager lbcManager;
	
	
	
	public int what = -1;
	public Bundle bundle = null;
	
	Protocol(){
	
	}
	
	
	
	public void processInput(byte[] msg){
		
		// Format: 2 bit version, 6 bit command,  others are command specific 

		int version =  (msg[0] >> 6);
		int command =  (msg[0] << 2 >> 2 );
		Log.d(TAG, "FPA version:"+version+" command:"+command );
		if( version == 0){ // Currently, only version 00 implemented
			int groupId = (int)msg[1];
			int userId = (int)msg[2];
			ByteBuffer ba = ByteBuffer.wrap(msg);
			
			switch(command){
			case BROADCAST_LOCATION:
				/*
				  0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
				| ver  |0  0  0  1  0  1|       group id         |        user id        |    millionSecond     ~         				 
				|                                  m i l l i o n S e c o n d                                    ~                        
				|                           millionSecond                                |    latitude			~
				|                             latitude                                   |    longitude         ~
				|                             longitude                                  |    altitude          ~
				|                             altitude                                   |    accuracy          ~
				|                             accuracy                                   |				
				*/
				
				long mills = ba.getLong(3);
				float latitude = ba.getFloat(11);
				float longitude = ba.getFloat(15);
				float altitude = ba.getFloat(19);
				float accuracy = ba.getFloat(23);
				
				
				what = FpaService.MSG_DRAW_MARKER;
				bundle = new Bundle();
								    			
    			bundle.putInt("userCode", userId);
    			bundle.putDouble("lat", latitude);
    			bundle.putDouble("lng", longitude);
    			bundle.putDouble("timestamp", mills);
		
				
//				Intent intent = new Intent(ACTION_UPDATE_PEER_LOCATION);
//				intent.putExtra(EXTRA_GROUP_ID, groupId);
//				intent.putExtra(EXTRA_USER_ID, userId);
//				intent.putExtra(EXTRA_MILLIS, mills);
//				intent.putExtra(EXTRA_LATITUDE, latitude);
//				intent.putExtra(EXTRA_LONGITUDE, longitude);
//				intent.putExtra(EXTRA_ALTITUDE, altitude);
//				intent.putExtra(EXTRA_ACCURACY, accuracy);
//
//				Log.d(TAG, "send intent broadcast location ");
//				lbcManager.sendBroadcast(intent);
				
				
				
				
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

	
	public void processOutput(){
		
	}
	
	public int getMsgType(){
		return what;
	}
	
	public Bundle getBundle(){
		return bundle;
	}
	
	public void clear(){
		what = -1;
		bundle = null;
	}




}
