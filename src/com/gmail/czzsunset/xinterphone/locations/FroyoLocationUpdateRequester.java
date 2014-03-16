/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.czzsunset.xinterphone.locations;

import android.app.PendingIntent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import com.gmail.czzsunset.xinterphone.Constants;
import com.gmail.czzsunset.xinterphone.locations.base.LocationUpdateRequester;

/**
 * Provides support for initiating active and passive location updates 
 * optimized for the Froyo release. Includes use of the Passive Location Provider.
 * 
 * Uses broadcast Intents to notify the app of location changes.
 */
public class FroyoLocationUpdateRequester extends LocationUpdateRequester{
	
	public final static String TAG = "FroyoLocationUpdateRequester" ;

  public FroyoLocationUpdateRequester(LocationManager locationManager) {
    super(locationManager);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void requestPassiveLocationUpdates(long minTime, long minDistance, PendingIntent pendingIntent) {
    // Froyo introduced the Passive Location Provider, which receives updates whenever a 3rd party app 
    // receives location updates.
    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, Constants.MAX_TIME, Constants.MAX_DISTANCE, pendingIntent);    
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void requestLocationUpdates(long minTime, long minDistance, Criteria criteria, LocationListener listener, Looper looper) {
	  // Gingerbread supports a location update request that accepts criteria directly.
	  // Note that we aren't monitoring this provider to check if it becomes disabled - this is handled by the calling Activity.	  
	  //	  locationManager.requestLocationUpdates(minTime, minDistance, criteria, l);
	  
//	  locationManager.requestLocationUpdates(0, 0, criteria, listener, looper);
	  
      String provider = locationManager.getBestProvider(criteria, true);
      Log.d(TAG,"criteria"+criteria + "  Provider:"+provider);
      
      if (provider != null){
//    	  locationManager.requestLocationUpdates(minTime, minDistance, criteria, listener, looper);
    	  locationManager.requestLocationUpdates(provider, minTime, minDistance, listener, looper);
      }
  }    
  
}
