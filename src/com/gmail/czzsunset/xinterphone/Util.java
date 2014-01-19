package com.gmail.czzsunset.xinterphone;

import android.content.Context;

import com.gmail.czzsunset.xinterphone.utils.Installation;



public class Util {
	/**
	 * Convert a byte array to a long value; 
	 * @param buf byte array 
	 * @param offset  the start index of the byte array to convert 
	 * @return
	 */
	public static final long toLong(byte[] buf, int offset){
		
		long l = ((buf[offset+0] & 0xFFL) << 56) |
		         ((buf[offset+1] & 0xFFL) << 48) |
		         ((buf[offset+2] & 0xFFL) << 40) |
		         ((buf[offset+3] & 0xFFL) << 32) |
		         ((buf[offset+4] & 0xFFL) << 24) |
		         ((buf[offset+5] & 0xFFL) << 16) |
		         ((buf[offset+6] & 0xFFL) <<  8) |
		         ((buf[offset+7] & 0xFFL) <<  0) ;
		return l;
	}
	
	public static final String getUuid(Context context){
	    return Installation.id(context);
	}

    
	

}
