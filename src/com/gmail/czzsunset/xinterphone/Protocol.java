package com.gmail.czzsunset.xinterphone;

import java.nio.ByteBuffer;

import com.gmail.czzsunset.xinterphone.model.SimpleUser;



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
	
	public SimpleUser peer = null;
	
	Protocol(){
	
	}
	
	
	
	public void processInput(byte[] msg){
		
		// Format: 2 bit version, 6 bit command,  others are command specific 

		int version =  (msg[0] >> 6);
		int command =  (msg[0] << 2 >> 2 );
		Log.d(TAG, "FPA version:"+version+" command:"+command );
		if( version == 0){ // Currently, only version 00 implemented, test version, does not consider implemention
			int groupId = (int) msg[1];
			int userId = (int)msg[2];
			int iUUID = (int)msg[3];
			
			ByteBuffer ba = ByteBuffer.wrap(msg);
			
			switch(command){
			case BROADCAST_LOCATION:
				/*
				  0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31
				| ver  |0  0  0  1  0  1|     group id           |   userCode            |          iUUID       |      
				|                             millionSecond                                                     ~
				|                             millionSecond                                                     |				
				|                             latitude                                                          |
				|                             longitude                                                         | 
				|                             altitude				                                            |
				|                             accuracy                                                          |
			    -------------------------------------------------------------------------------------------------				
				*/
				
				
				long mills = ba.getLong(4);
				float latitude = ba.getFloat(12);
				float longitude = ba.getFloat(16);
				float altitude = ba.getFloat(20);
				float accuracy = ba.getFloat(24);
				
				
				peer  = new SimpleUser();
				peer.iUUID = iUUID;
				peer.userCode = userId;
				peer.latitude = latitude;
				peer.longitude = longitude;
				peer.altitude = altitude;
				peer.timestamp = mills;
				
						
				
				
				
				
//				what = FpaService.MSG_DRAW_MARKER;
//				bundle = new Bundle();
//								    			
//    			bundle.putInt("userCode", userId);
//    			bundle.putDouble("lat", latitude);
//    			bundle.putDouble("lng", longitude);
//    			bundle.putDouble("timestamp", mills);
		
				
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

	
	public SimpleUser getPeer(){		
		return peer;		
	}
	
	public byte[] processOutput(int version, int command, SimpleUser user){
		byte[] msg  = null;
		if(version == 0){
		
			switch(command){
			
			case BROADCAST_LOCATION:
				msg = new byte[28];
				ByteBuffer bb = ByteBuffer.allocate(28);
				
				bb.put((byte) 0x05); // version & command
				bb.put((byte) 0); // groupID
				bb.put((byte) peer.userCode); // 				
				bb.put((byte) peer.iUUID); // iUUID = 32
				
				bb.putLong( peer.timestamp);
				bb.putFloat((float) peer.latitude);  // latitude
				bb.putFloat((float) peer.longitude);  // longitude
				bb.putFloat((float) peer.altitude);  // altitude
				bb.putFloat((float) 10);  // accuracy 
				
				msg = bb.array();	
				
				break;
				
			default:
				break;					
				
			}

		}
		return msg;
	}
	
	
	
	public void clear(){		
		peer = null;
	}




}
