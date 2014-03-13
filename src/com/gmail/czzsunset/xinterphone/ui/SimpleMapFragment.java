package com.gmail.czzsunset.xinterphone.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.gmail.czzsunset.xinterphone.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public  class SimpleMapFragment extends SupportMapFragment {

	
	public static final String TAG = "SimpleMapFragment";
	
	
	public static SimpleMapFragment mSimpleFrag;
	
	private static MapFragment mMapFrag;
	
	
	private boolean bMapWillFollow = true;
	
//	private Map<Integer, Marker> mMarkerDict = new HashMap<Integer, Marker>(); 
	SparseArray<Marker> mMarkers = new SparseArray<Marker>();
	
	public enum MarkerColor{
		RED,
		BLUE;
	}

	
	public static SimpleMapFragment newInstance(){
		// If there is no lastKnown location, the default location is ShenZhen 
		LatLng latlng = new LatLng(22.5825,113.9506);
		return newInstance(latlng);		
		
	}
	
	public static SimpleMapFragment newInstance(LatLng latlng){
		
		Log.i(TAG,"newInstance at "+latlng);
    	GoogleMapOptions opts = new GoogleMapOptions();
    	
    	CameraPosition camera = new CameraPosition.Builder()
    								.target(latlng)
    								.zoom(20)    							
    								.build();
    	
    	
    	opts.camera(camera)
			.compassEnabled(true)
			.rotateGesturesEnabled(true)						
			.tiltGesturesEnabled(true)   ;
		
		Log.d(TAG, "opts:" + opts);
//		SupportMapFragment fr = SupportMapFragment.newInstance(opts);
//		Log.d(TAG, "supportMapFragment:" + fr);
		
		
		return new SimpleMapFragment();
		
//		if( mSimpleFrag == null){			
//			mSimpleFrag =   new SimpleMapFragment();			
//		}
//		
//		return mSimpleFrag;
	}
	
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		Log.i(TAG,"onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView");
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setUpMapIfNeeded();
		return view;
	}
	
	@Override
	public void onResume(){		
		super.onResume();
		
		Log.d(TAG,"onResume");
		
		setUpMapIfNeeded();
		

		
		
		
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
//		if( prefs.getBoolean("firstLaunch", true)){
//			Intent i = new Intent(this, SimplePrefActivity.class);									
//			startActivity(i);			
//			finish();			
//			return;
//		}

		
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		Log.i(TAG,"onAttach");
	}
	
	@Override
	public void onDetach(){
		super.onDetach();
		Log.i(TAG,"onDetach");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.i(TAG,"onDestroy");
	}

	
	
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Rect bounds = new Rect();
	

	
	
	public void addMarker(int iUUID, int userCode, double lat, double lng, String name, String snippet, MarkerColor color){
		
		
		Log.d(TAG, "addMarker");
		Log.d(TAG, "all exists markers:" + mMarkers);
		if( mMap != null){			
			
			Log.d(TAG, "Show :"+ userCode + " " + name +" on lat:" + lat + " lng:" + lng + " uuid:" + iUUID);
									
			LatLng latlng = new LatLng(lat,lng);
			Marker marker = mMarkers.get(iUUID, null);
			if(marker != null){
				// marker already exists
				marker.setPosition(latlng);
				return;
			}
			
			//Bitmap bitmap = base.copy(Bitmap.Config.ARGB_8888, true);

			BitmapFactory.Options options = new BitmapFactory.Options();
			
			options.inMutable = true;
			
			
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 
					(color == MarkerColor.RED) ? R.drawable.flag_red: R.drawable.flag_blue , options);

			
			
			String text = String.valueOf(userCode);
			paint.getTextBounds(text, 0, text.length(), bounds);
			float x = bitmap.getWidth() / 2.0f;
			float y = (bitmap.getHeight() - bounds.height()) / 2.0f - bounds.top;
	
			
			paint.setColor(Color.WHITE);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(getResources().getDimension(R.dimen.text_size));					
			
			Canvas canvas = new Canvas(bitmap);
			canvas.drawText(text, x, y, paint);
	
			
			BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
			
						
			marker = mMap.addMarker(new MarkerOptions()
		     		.position(latlng)
		     		.title(name)
		     		.snippet(snippet)
		     		.icon(icon)
		     		.anchor(0.5f, 0.5f));
			
			
			mMarkers.append(iUUID, marker);

			
		}else{
			Log.d(TAG, "mMap is null");
		}		
	}
	
	
	
	public void updateMarker(int userCode, double newLat, double newLng,
			double timestamp) {
		// TODO Auto-generated method stub
		
//		mMap.clear()
		
	}  	
	
	public void animateMarker(int iUUID, double dstLat, double dstLng, long durationMs, boolean mapWillFollow){
		Log.d(TAG,"animateMarker");
		
		Log.d(TAG,"iUUID:" + iUUID + "mMarkers" + mMarkers);
		
		final Marker marker =  mMarkers.get(iUUID);
		if(marker != null){
			Log.d(TAG,"bMapWillFollow:" + bMapWillFollow);
			animateMarker(marker, dstLat, dstLng, durationMs, false, mapWillFollow);
		}else{
			Log.d(TAG,"marker is null");
		}
		
	}
	
    public void animateMarker(final Marker marker, final double dstLat, final double  dstLng,
    					final long durationMs,  final boolean hideMarker, final boolean mapWillFollow) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
      

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / durationMs);
                double lng = t * dstLng + (1 - t)
                        * startLatLng.longitude;
                double lat = t * dstLat + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                
                if( mapWillFollow){
                	mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)) );
                }

                if (t < 1.0) {
                    // Post again 10ms later.
                    handler.postDelayed(this, 5);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
                
                
                
            }
        });
    }	
	
	
	
	public void animateToLocation(LatLng latlng, int durationMs){
		
		if( mMap != null){
			
	    	if( latlng != null){	    		
	    		Log.d(TAG, "Animate to location:"+latlng);

	    		if( durationMs == 0){
	    			mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng) );
	    		}else{	    			
	    			mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng), durationMs, null );
	    		}
	    		
	    		
	    	}	    	
	    }			
	}
	
	

	
    private GoogleMap mMap;
    
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
    	Log.d(TAG, "setUpMapIfNeeded");
    	
        if (mMap == null) {
        	
        	Log.d(TAG,"google map is null");
        	
        	FragmentManager manager = getActivity().getSupportFragmentManager();
        	SimpleMapFragment mapfrag = (SimpleMapFragment) manager.findFragmentByTag("tabmap");
        	        	
        	
        	if( mapfrag != null){
        		
        		Log.d(TAG, "mapfrag is not null");
        		
        		mMap = mapfrag.getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    // The Map is verified. It is now safe to manipulate the map.
                		
                		Log.i(TAG,"Got GooogleMap");
                		
                		mMap.setMyLocationEnabled(true);
                		
                		float maxZoom = mMap.getMaxZoomLevel();
                		
                		
//                		CameraUpdate cam = CameraUpdateFactory.zoomTo(maxZoom - 4);
                		
                		CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(new LatLng(22.5825,113.9506), maxZoom-4);
                		mMap.moveCamera(cam);
                		
//                		mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
                		
                		UiSettings mUiSetting = mMap.getUiSettings();	
                    	mUiSetting.setMyLocationButtonEnabled(true);
                    	mUiSetting.setCompassEnabled(true);                    	
                    	
                    	mUiSetting.setAllGesturesEnabled(true);       
                    	
                    	
//                    	mMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener(){
//
//							@Override
//							public boolean onMyLocationButtonClick() {								
//								Log.d(TAG,"onMyLocationButtonClick");
//								bMapWillFollow = true;
//								return false;
//							}														                    		
//                    	});
//                    	
//                    	mMap.setOnCameraChangeListener(new OnCameraChangeListener(){
//
//							@Override
//							public void onCameraChange(CameraPosition camPos) {
//								Log.d(TAG,"onCameraChange:" + camPos);
//								bMapWillFollow = false;
//							}
//                    		
//                    	});
                    	
                    	

                }else{
                	Log.d(TAG, "Did not get Google Map");
                }
        		
        	}else{
        		Log.d(TAG,"mapfrag is null");
        	}
        		
        }else{
        	Log.d(TAG, "Google map is not null");
        }
    }

  	
	
	
	
}
