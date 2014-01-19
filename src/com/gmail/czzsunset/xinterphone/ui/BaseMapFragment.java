package com.gmail.czzsunset.xinterphone.ui;






import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.czzsunset.xinterphone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;




public class BaseMapFragment  extends SupportMapFragment {
		
	private static final String TAG 					= 	"BaseMapFragment";
	private static final String ARG_MAP_LATLNG			=	"ARG_MAP_LATLNG";

	
	
	private GoogleMap mMap;
	private UiSettings mUiSetting;
	
	private LatLng latlng = new LatLng(0f, 0f);;
	private int zoomlevel = 16;
	
	
	
	
	private static final int[] res = { R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5 };	
	
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Rect bounds = new Rect();
	
	
	private static BaseMapFragment fragment;
	

		
	public static BaseMapFragment newInstance(){
		
		
		fragment = (BaseMapFragment) SupportMapFragment.newInstance();
		
		return fragment;
				
	}
	
	public void setDeferredZoomLevel(int level){
		zoomlevel = level;
	}
	public void setDeferredLatLng(LatLng latlng){
		this.latlng = latlng; 
	}
	
	@Override
	public 
	void onCreate(Bundle bundle){
		Log.d(TAG, "onCreate");
		super.onCreate(bundle);
		
		
		
/*		Bundle args = getArguments();
		if( args != null){
			double latlng[] = args.getDoubleArray(ARG_MAP_LATLNG);
			inPos = new LatLng(latlng[0], latlng[1]);
		}*/
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onStart(){
		Log.d(TAG,"onStart");
		super.onStart();
		

		
	}
	
	@Override
	public void onResume(){
		Log.d(TAG, "onResume");		
		super.onResume();
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
		super.onDetach();
	}
	
	
	


	public void animateToLocation(LatLng latlng, int durationMs){
		if( mMap != null){
			
	    	if( latlng != null){	    		
	    		Log.d(TAG, "Animate to last known place:"+latlng.toString());
	    		if( durationMs == 0){
	    			mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng) );
	    		}else{
	    			mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng), durationMs, null );
	    		}
	    		
	    		
	    	}	    	
	    }			
	}
	
	/**
	 * Animate to the given location
	 * @param location:	The destination location
	 * @param durationMs:	duration to animate to destination 		
	 */
	public void animateToLocation(Location location, int durationMs){
		if(location !=null){
			LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());	
			animateToLocation(latlng, durationMs);
		}
	}
	
//	
//		public void assignSelfMap(){
//			mMap = this.getMap();
//		}
//	
	/**
	 * Get the Map and UiSettings object if needed
	 */
	public void setUpMapIfNeeded() {
	    // Do a null check to confirm that we have not already instantiated the
	    //  map.
		Log.d(TAG, "setUpMapIfNeeded");
	    if (mMap == null) {
	    	Log.d(TAG, "map is null, try to get it");
	        // Try to obtain the map from the SupportMapFragment.
	        mMap = this.getMap();
	        	        
	        // Check if we were successful in obtaining the map.
	        if (mMap != null) {
	        	Log.d(TAG, "Got map");
	        	 setUpMap();
	        }else{
	        	Log.d(TAG, "Can not get  map");
	        }
	    }else{
	    	Log.d(TAG, "map is not null");
	    }
	}
	
	public void setUpMap(){
		mMap.setMyLocationEnabled(true);
		Log.d(TAG, "Move camera to:"+latlng.toString() +" zoom level:"+zoomlevel );
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomlevel));
		
		mUiSetting = mMap.getUiSettings();	
    	mUiSetting.setMyLocationButtonEnabled(true);
    	
    	
    	
	}
	
	
	public void addMarker2(double lat, double lng, int indexInGroup, String name,
																		String snippet, boolean isSelf) {


		if( mMap != null){
			
			
			Log.d(TAG, "Show :"+name+" on lat:" + lat + " lng:" + lng);
			
			
			
			//Bitmap bitmap = base.copy(Bitmap.Config.ARGB_8888, true);

			BitmapFactory.Options options = new BitmapFactory.Options();
			
			options.inMutable = true;
			
			
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 
									isSelf? R.drawable.flag_red: R.drawable.flag_blue , options);

			LatLng location = new LatLng(lat,lng);
			
			String text = String.valueOf(indexInGroup);
			paint.getTextBounds(text, 0, text.length(), bounds);
			float x = bitmap.getWidth() / 2.0f;
			float y = (bitmap.getHeight() - bounds.height()) / 2.0f - bounds.top;
	
			
			paint.setColor(Color.WHITE);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(getResources().getDimension(R.dimen.text_size));					
			
			Canvas canvas = new Canvas(bitmap);
			canvas.drawText(text, x, y, paint);
	
			
			BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
			
						
			mMap.addMarker(new MarkerOptions()
		     		.position(location)
		     		.title(name)
		     		.snippet(snippet)
		     		.icon(icon)
		     		.anchor(0.5f, 0.5f));	
			if(isSelf){
				animateToLocation(location, 1000);
			}
			
		}else{
			Log.d(TAG, "mMap is null");
		}
			
		
		
	}	

	public void addMarker(Location loc, int markerSize, int indexInGroup, String name,
			String snippet){
		addMarker(new LatLng(loc.getLatitude(),loc.getLongitude()),
								markerSize, indexInGroup, name, snippet);
									
		
	}
	
	public void addMarker(double lat, double lng, int markerSize, int indexInGroup, String name,
			String snippet){
		addMarker(new LatLng(lat,lng), markerSize, indexInGroup, name, snippet);
	}
	
	public void addMarker(double lat, double lng, int indexInGroup){
		
		addMarker(lat,lng,indexInGroup>=10?2:1,indexInGroup,null,null);
	}



	/**
	 * Add a marker to Map
	 * @param location
	 * @param markerSize
	 * @param indexInGroup
	 * @param name
	 * @param snippet
	 */
	public void addMarker(LatLng location, int markerSize, int indexInGroup, String name,
			String snippet) {


		if( mMap != null){
			
			
			Log.d(TAG, "Show :"+name+" on location:"+location.toString() );
			
			
			//Bitmap bitmap = base.copy(Bitmap.Config.ARGB_8888, true);

			BitmapFactory.Options options = new BitmapFactory.Options();
			
			options.inMutable = true;
			
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res[markerSize-1], options);

			
			
			String text = String.valueOf(markerSize);
			paint.getTextBounds(text, 0, text.length(), bounds);
			float x = bitmap.getWidth() / 2.0f;
			float y = (bitmap.getHeight() - bounds.height()) / 2.0f - bounds.top;
	
			
			paint.setColor(Color.WHITE);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(getResources().getDimension(R.dimen.text_size));					
			
			Canvas canvas = new Canvas(bitmap);
			canvas.drawText(text, x, y, paint);
	
			
			BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
			
						
			mMap.addMarker(new MarkerOptions()
		     		.position(location)
		     		.title(name)
		     		.snippet(snippet)
		     		.icon(icon)
		     		.anchor(0.5f, 0.5f));	
			
			animateToLocation(location, 1000);
			
		}else{
			Log.d(TAG, "mMap is null");
		}
			
		
		
	}	
	
	
	
	
	public void removeAllMarkers(){
		mMap.clear();
	}
	
	public void clearMap(){
		mMap.clear();
	}
	
/*	public void drawTrack(ArrayList<LatLng> locs){
		
		PolylineOptions options = new PolylineOptions();
		
		for(int i=0;i<locs.size();i++){
			mMap.addMarker(new MarkerOptions()
				.position(locs.get(i))				
			);	
			
			options.add(locs.get(i));
		}
		
		
		Polyline polyline = mMap.addPolyline(options);
		
	}*/
	
	public void drawTrack(ArrayList<Location> locs){
		PolylineOptions options = new PolylineOptions();
		for(int i=0;i<locs.size();i++){
			LatLng lat = new LatLng(locs.get(i).getLatitude(), locs.get(i).getLongitude());
			mMap.addMarker(new MarkerOptions()
							.position(lat));
						
			options.add(lat);
		}
		Log.d(TAG, "drawTrack, track size"+locs.size() );
		Polyline polyline = mMap.addPolyline(options);
		
	}
	
	public void drawMyIconOnMap(Location loc){
		
		addMarker(loc, 4, 0, "ME","TEST");
		
	}
	


	
}



	