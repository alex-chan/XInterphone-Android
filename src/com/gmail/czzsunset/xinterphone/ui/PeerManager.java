package com.gmail.czzsunset.xinterphone.ui;

import java.util.ArrayList;

import com.gmail.czzsunset.xinterphone.model.SimpleUser;

import android.os.Bundle;
import android.util.SparseArray;

public class PeerManager {

	

	
	ArrayList<SimpleUser> mPeers = new ArrayList<SimpleUser>();
	
//	private SparseArray<SimpleUser> mPeers ;
//	private SimpleUser mMySelf;
	
	public SimpleUser buildPeer(final int iUUID, final int userCode, double latitude,
			double longitude, double altitude, long timestamp, boolean isMyself){
		SimpleUser peer  = new SimpleUser();
		peer.iUUID = iUUID;
		peer.userCode = userCode;
		peer.latitude = latitude;
		peer.longitude = longitude;
		peer.altitude = altitude;
		peer.timestamp = timestamp;
		peer.isMySelf = isMyself;
		return peer;
	}	
	
	public void updateMySelf(final int iUUID, final int userCode, double latitude,
				double longitude, double altitude, long timestamp){
		
		updatePeer(iUUID, userCode, latitude, longitude, altitude, timestamp, true);
	}
	
	
	public void updatePeer(SimpleUser peer){
		
		boolean bFound = false;
		
		for(int i=0; i<mPeers.size(); i++){
			SimpleUser peer1 = mPeers.get(i);
			if( peer1.iUUID == peer.iUUID){				
				mPeers.set(i, peer1);				
				bFound = true;
				break;
			}
		}
		
		if(!bFound){
			mPeers.add( peer );
		}		
		
	}
	
	public void updatePeer(final int iUUID, final int userCode, double latitude,
			double longitude, double altitude, long timestamp, boolean isMyself){
		
		boolean bFound = false;
		
		
		for(int i=0; i<mPeers.size(); i++){
			SimpleUser peer = mPeers.get(i);
			if( peer.iUUID == iUUID){
				
				peer = buildPeer(iUUID,userCode,latitude,longitude,altitude,timestamp,isMyself);
				mPeers.set(i, peer);
				bFound = true;
				break;
			}
		}
		
		if(!bFound){
			mPeers.add( buildPeer(iUUID,userCode,latitude,longitude,altitude,timestamp,isMyself) );
		}
	
	}
	
	public Bundle getBundle(final int iUUID){
		return getBundle(iUUID,0);
	}
	
	public Bundle getBundle(final int iUUID, int version){
		
		Bundle bundle = new Bundle();
		
		boolean bFound = false;
		int peerIndex = -1;
		
		for(int i=0; i<mPeers.size(); i++){
			SimpleUser peer = mPeers.get(i);
			if( peer.iUUID == iUUID){
				
				bFound = true;
				peerIndex = i;
				break;
			}
		}	
		if( !bFound){
			// TODO: raise a Exception, should
			return null;
		}
		
		switch(version){
		case 0:
			
			SimpleUser peer1 = mPeers.get(peerIndex);
			
			bundle.putInt("iUUID", iUUID);
			bundle.putInt("userCode", peer1.userCode);
			bundle.putDouble("lat", peer1.latitude);
			bundle.putDouble("lng", peer1.longitude);
			bundle.putDouble("timestamp", peer1.timestamp);			
			
			break;
			
		default:
			bundle = null;
				
		}

		return bundle;
		
		
	}
	
	
	public Bundle getAllPeerBundle(){
		
		Bundle bundle  = new Bundle();
		int size = mPeers.size();
		bundle.putInt("markerSize", size);
		
		Bundle bdl;
		SimpleUser peer;
		
		for(int i=0;i<size;i++){
			bdl = new Bundle();
			peer = mPeers.get(i);
			bdl.putInt("iUUID", peer.iUUID);
			bdl.putInt("userCode", peer.userCode);
			bdl.putDouble("lat",peer.latitude);
			bdl.putDouble("lng",peer.longitude);
			bdl.putDouble("alt",peer.altitude);			 
			bdl.getDouble("timestamp", peer.timestamp);			
			
			bundle.putBundle(String.valueOf(i), bdl);
		}
		
		
		return bundle;
	}
	
	
}
