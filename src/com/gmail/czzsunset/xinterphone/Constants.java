package com.gmail.czzsunset.xinterphone;

import android.app.AlarmManager;

public class Constants {

	public final static String PACKAGE_PREFIX = "com.gmail.czzsunset.xinterphone.";
	
	
	
	public final static String CURRENT_GROUP_ID =  "CURRENT_GROUP_ID";
	
	public final static String SHARED_PREF_NAME = "SharedPreferenceOfSunset";
	public final static String SHARED_PREF_INTERCOM = "SharedPreferenceInterphone";
	
	public final static String IC_ENCRY_TYPE = "IntercomEncryptionType";
	public final static String IC_UPDATE_INTERVAL = "IntercomUpdateInterval";
	public final static String IC_FREQ     =   "IntercomFrequence";



    public static final String PROTOCOL_HTTP = "http";
    public static final String SERVER_HOST = "192.168.2.11";
    public static final String HTTP_API_PATH = "api";
    public static final int SERVER_PORT = 8090;
    public static final String CHARSET = "UTF-8";
    public static int HTTP_CONNECT_TIMEOUT = 5000;
    
    
    
    
	public static long INTERVAL_UPDATE_LOCATION = 2 * 60 * 1000; // 2 minutes
	
	// TODO Turn off when deploying the app
	public static boolean DEVELOPER_MODE = true;

	
	  /**
	   * These values control the user experience of your app. You should
	   * modify them to provide the best experience based on how your
	   * app will actually be used.
	   * TODO Update these values for your app.
	   */
	  // The default search radius when searching for places nearby.
	  public static int DEFAULT_RADIUS = 1500;
	  // The maximum distance the user should travel between location updates. 
	  public static int MAX_DISTANCE = DEFAULT_RADIUS/2;
	  // The maximum time that should pass before the user gets a location update.
	  public static long MAX_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	  
	
	public static boolean SUPPORTS_HONEYCOMB = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;
	public static boolean SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;	
	public static boolean SUPPORTS_FROYO = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO;
	public static boolean SUPPORTS_ECLAIR = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR;	

    
    
    
	
}
