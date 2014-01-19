package com.gmail.czzsunset.xinterphone.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.czzsunset.xinterphone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public  class SimpleMapFragment extends SupportMapFragment {

	
	public static final String TAG = "SimpleMapFragment";
	
	
	public static SimpleMapFragment mSimpleFrag;
	
	private static MapFragment mMapFrag;
	

	
	public static SimpleMapFragment newInstance(){
		SupportMapFragment.newInstance();
		if( mSimpleFrag == null){			
			mSimpleFrag =   new SimpleMapFragment();			
		}
		
		return mSimpleFrag;
		
	}
	
	public static SimpleMapFragment newInstance(LatLng latlng){
    	GoogleMapOptions opts = new GoogleMapOptions();
    	
    	CameraPosition camera = new CameraPosition.Builder()
    								.target(latlng)
    								.zoom(14)    							
    								.build();
    	
    	
    	opts.camera(camera)
			.compassEnabled(true)
			.rotateGesturesEnabled(true)
			.tiltGesturesEnabled(true)   ;
		
		
		SupportMapFragment.newInstance(opts);
		if( mSimpleFrag == null){			
			mSimpleFrag =   new SimpleMapFragment();			
		}
		
		return mSimpleFrag;
	}
	
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
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
	
	

	
	
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Rect bounds = new Rect();
	
	public void addMarker(double lat, double lng, int indexInGroup, String name,
																		String snippet, boolean isSelf) {


		if( mMap != null){
			
			
			Log.d(TAG, "Show :"+ indexInGroup + " " + name +" on lat:" + lat + " lng:" + lng);
			
			
			
			//Bitmap bitmap = base.copy(Bitmap.Config.ARGB_8888, true);

			BitmapFactory.Options options = new BitmapFactory.Options();
			
			options.inMutable = true;
			
			
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 
									isSelf? R.drawable.flag_red: R.drawable.flag_blue , options);

			LatLng latlng = new LatLng(lat,lng);
			
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
		     		.position(latlng)
		     		.title(name)
		     		.snippet(snippet)
		     		.icon(icon)
		     		.anchor(0.5f, 0.5f));	
			if(isSelf){
				animateToLocation(latlng, 1000);
			}
			
		}else{
			Log.d(TAG, "mMap is null");
		}
			
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
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	FragmentManager manager = getActivity().getSupportFragmentManager();
        	SimpleMapFragment mapfrag = (SimpleMapFragment) manager.findFragmentByTag("tabmap");
        	
        	if( mapfrag != null){
        		mMap = mapfrag.getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    // The Map is verified. It is now safe to manipulate the map.
                		
                		Log.i(TAG,"Got GooogleMap");
                		
                		mMap.setMyLocationEnabled(true);
                		
                		mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
                		
                		UiSettings mUiSetting = mMap.getUiSettings();	
                    	mUiSetting.setMyLocationButtonEnabled(true);
                    	mUiSetting.setCompassEnabled(true);                    	
                    	
                    	mUiSetting.setAllGesturesEnabled(true);           
                    	
                    	

                }
        		
        	}
        		
        }
    }    	
	
	
	
}
