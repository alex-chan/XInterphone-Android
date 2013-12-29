package com.gmail.czzsunset.xinterphone.ui;

import com.gmail.czzsunset.xinterphone.R;
import com.gmail.czzsunset.xinterphone.locations.PlatformSpecificImplementationFactory;
import com.gmail.czzsunset.xinterphone.locations.base.ILastLocationFinder;

import java.lang.reflect.Field;
import java.util.Date;



import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class TabMap extends Fragment {

		

		private static final String TAG 					= "TabMap";		
		private static final String MAP_FRAGMENT_TAG		= "MAP_FRAGMENT_TAG"; 
		
		


//		private ArrayList<Friend> friends;
		BaseMapFragment mMapFragment ;
        private ArrayAdapter<String> mSpinnerAdapter;

        private ILastLocationFinder mLastLocationFinder;

        private class MyLocationListener implements LocationListener{

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				Log.d(TAG, "A location fix coming...");
				mMapFragment.animateToLocation(location, 1000);
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
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
        	
        };
		
		@Override
		public void onCreate(Bundle savedInstanceState){
			Log.d(TAG, "onCreate");
			super.onCreate(savedInstanceState);
			
			setHasOptionsMenu(true);		    							    
		    
		    
			mMapFragment = attachBaseMapFragment();



			
			
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Log.d(TAG, "onCreateView");					
			
		    View v = inflater.inflate(R.layout.fragment_main_tabmap, container, false);


		    
		    
		    return v;
		}
		

		@Override
		public void onStart() {
			Log.d(TAG, "onStart");
			
		    super.onStart();		   
		    
		    
		    
		}
		
		
		
		@Override
		public void onResume(){
			Log.d(TAG, "onResume");
			
			super.onResume();
			
			mMapFragment.setUpMapIfNeeded();		
			
			
			mLastLocationFinder = PlatformSpecificImplementationFactory.getLastLocationFinder(getActivity());
			
			mLastLocationFinder.setChangedLocationListener(new MyLocationListener());
			
			Date dt = new Date();
			Log.d(TAG, ""+ dt.getTime());
			Log.d(TAG, dt.getTime() - 120 * 1000 + "" );
			Location loc = mLastLocationFinder.getLastBestLocation(2000, dt.getTime() - 120 * 1000);
			mMapFragment.animateToLocation(loc, 0);
					
		}
		
		@Override
		public void onPause(){
			Log.d(TAG, "onPause");
			super.onPause();
		}
		
		
		@Override
		public void onStop(){
			Log.d(TAG, "onStop");
			super.onStop();

		}
		

		@Override
		public void onDestroyView(){
			Log.d(TAG, "onDestroyView");
			super.onDestroyView();
		}
		
		@Override
		public void onDestroy(){
			Log.d(TAG, "onDestroy");
			super.onDestroy();
			

		}
		
		
		@Override
		public void onDetach() {
			Log.d(TAG, "onDetach");
			/*
			 * Fix bug of android support library v4:
			 * http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed/15656428#15656428
			 */
			
		    super.onDetach();

		    try {
		        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
		        childFragmentManager.setAccessible(true);
		        childFragmentManager.set(this, null);

		    } catch (NoSuchFieldException e) {
		        throw new RuntimeException(e);
		    } catch (IllegalAccessException e) {
		        throw new RuntimeException(e);
		    }
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		    Log.d(TAG,"onCreateOptionsMenu");
		    
		    
            mSpinnerAdapter = new ArrayAdapter<String>(
                    ((MainActivity)getActivity()).getActionBarThemedContextCompat(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    getResources().getStringArray(R.array.tabmap_menus));   	        
		    
            inflater.inflate(R.menu.tabmap, menu);
            
		    MenuItem menuItem = menu.findItem(R.id.action_map_spinner);
		    Spinner spinner = (Spinner) menuItem.getActionView();
		    		   
		    spinner.setAdapter(mSpinnerAdapter);
		    
		    
		    super.onCreateOptionsMenu(menu, inflater);
		}
		
		/**
		 * Attach the CommonMapFragment instance to current fragment if needed
		 * @return 
		 */
		public BaseMapFragment attachBaseMapFragment(){
			
			BaseMapFragment tmpMap = (BaseMapFragment) getChildFragmentManager()
												.findFragmentByTag(MAP_FRAGMENT_TAG);
			
			if( tmpMap == null){
				Log.d(TAG, "mCommMapFragment is null");
				tmpMap = BaseMapFragment.newInstance();
			        
			    // Then we add it using a FragmentTransaction.
			    getChildFragmentManager()
			        	.beginTransaction()
			        	.replace(R.id.fragment_map_container, tmpMap, MAP_FRAGMENT_TAG)
			        	.commit();
			    
//				    mCommMapFragment.setRetainInstance(true);
			    
			}else{
				Log.d(TAG, "mCommMapFragment is not null");
				
			}
			tmpMap.setUpMapIfNeeded();
			return tmpMap;
			
		}
		

		
	
		

	

		
		
		
}
