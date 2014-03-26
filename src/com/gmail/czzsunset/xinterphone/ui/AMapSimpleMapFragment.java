package com.gmail.czzsunset.xinterphone.ui;



import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapFragment;
import com.amap.api.maps.Projection;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.gmail.czzsunset.xinterphone.R;



public class AMapSimpleMapFragment extends SupportMapFragment implements LocationSource{

	
	public static final String TAG = "AMapSimpleMapFragment";
	
	public static OnLocationChangedListener pListener;
	
	private static MapFragment mMapFrag;
	
	
	private boolean bMapWillFollow = true;
	
//	private Map<Integer, Marker> mMarkerDict = new HashMap<Integer, Marker>(); 
	SparseArray<Marker> mMarkers = new SparseArray<Marker>();
	
	public static SupportMapFragment instance;
	public static SupportMapFragment getInstance() {
	    if (instance == null) {
	        instance = newInstance();
	    }
	    // NullPointerException in getMap() ???
	    // instance.getMap().addMarker(new MarkerOptions().position(new LatLng(41.1, 39)));
	    return instance;
	}

	
    private AMap mMap;
	
	public enum MarkerColor{
		RED,
		BLUE;
	}

	private class TouchViewWrapper extends FrameLayout{

		public TouchViewWrapper(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			Log.i(TAG,"MyLocationButtonWrapper");
		}
		
	    @Override
	    public boolean dispatchTouchEvent(MotionEvent ev) {
	    	
	    	
	    	switch(ev.getAction()){
	    	case MotionEvent.ACTION_DOWN:
	    		break;
	    	case MotionEvent.ACTION_UP:
		    	int x = (int)( ev.getX() / mScale );	    	
		    	int y = (int)( ev.getY() / mScale );
		    	if(mMyLocationBtnRect.contains(x,y)){
		    		bMapWillFollow = true;
		    		Log.i(TAG, "click my location botton");
		    	}else{
		    		bMapWillFollow = false;
		    	}
	    		break;
	    	}
	    
	        return super.dispatchTouchEvent(ev);

	    }
	}

	
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		Log.i(TAG,"onCreate");
		
		mScale = getResources().getDisplayMetrics().density;
		
		
		int widthPt = (int) (getResources().getDisplayMetrics().widthPixels / mScale);
		int topLeftX = widthPt - 50;
		int topLeftY = 0;
		int bottomRightX = widthPt ;
		int bottomRightY = 50;
		mMyLocationBtnRect = new Rect( topLeftX, topLeftY, bottomRightX, bottomRightY);
	}
	
	private View mOriginalView;
	private float mScale ;  
	private Rect mMyLocationBtnRect ;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView");
		mOriginalView = super.onCreateView(inflater, container, savedInstanceState);
		
		TouchViewWrapper touchview = new TouchViewWrapper(getActivity());
		
		touchview.setBackgroundColor(Color.BLUE);
		touchview.addView(mOriginalView);
		
		
		
//		FrameLayout flayout = new FrameLayout(getActivity());
//		
//		
//	
//		
//		RelativeLayout rlayout = new RelativeLayout(getActivity());
//
//	
//		float scale = getResources().getDisplayMetrics().density;
//		
//		Log.d(TAG, "scale:"+ scale);
//		MyLocationButtonWrapper btnFlayout = new MyLocationButtonWrapper(getActivity());
////		btnFlayout.setBackgroundColor(Color.RED);
//		
//		RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams((int)(40 * scale), (int)(40 * scale) );
//		
//		lParams.rightMargin = (int) (10 * scale);
//		lParams.topMargin = (int) (10 * scale);
//		lParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		
//		rlayout.addView(btnFlayout, lParams	);
//		
//		
//		
//		flayout.addView(mapView);
//		flayout.addView(rlayout);
//				
		
		setUpMapIfNeeded();
		return touchview;
	}
	
	
	@Override
	public View getView(){
		return mOriginalView;
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
			animateMarker(marker, dstLat, dstLng, durationMs, false, bMapWillFollow);
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
                
                if( bMapWillFollow){
                	float curZoom = mMap.getCameraPosition().zoom;
                	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), curZoom) );
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
	    		float curZoom = mMap.getCameraPosition().zoom;
	    		if( durationMs == 0){
	    			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, curZoom) );
	    		}else{	    			
	    			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, curZoom), durationMs, null );
	    		}
	    		
	    		
	    	}	    	
	    }			
	}
	
	

    
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
    	Log.d(TAG, "setUpMapIfNeeded");
    	
        if (mMap == null) {
        	
        	Log.d(TAG,"google map is null");
        	
        	FragmentManager manager = getActivity().getSupportFragmentManager();
        	AMapSimpleMapFragment mapfrag = (AMapSimpleMapFragment) manager.findFragmentByTag("tabmap");
        	        	
        	
        	if( mapfrag != null){
        		
        		Log.d(TAG, "mapfrag is not null");
        		
        		mMap = mapfrag.getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    // The Map is verified. It is now safe to manipulate the map.
                		
                		Log.i(TAG,"Got GooogleMap");
                		
//                		mMap.setMyLocationEnabled(true);
                		
                		float maxZoom = mMap.getMaxZoomLevel();
                		
                		
//                		CameraUpdate cam = CameraUpdateFactory.zoomTo(maxZoom - 4);
                		
                		CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(new LatLng(22.5825,113.9506), maxZoom-4);
                		
                		mMap.moveCamera(cam);
                		
//                		mMap.setLocationSource(this);
                		
//                		mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
                		
                		UiSettings mUiSetting = mMap.getUiSettings();	
                    	mUiSetting.setMyLocationButtonEnabled(true);
//                    	mMap.setMyLocationEnabled(true);
                    	mUiSetting.setCompassEnabled(true);                    	
                    	
                    	mUiSetting.setAllGesturesEnabled(true);       
                    	
                    	
                    	mMap.setOnCameraChangeListener(new OnCameraChangeListener(){

							@Override
							public void onCameraChange(CameraPosition arg0) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onCameraChangeFinish(CameraPosition arg0) {
								// TODO Auto-generated method stub
								bMapWillFollow = false;
							}
                    		
                    	});
                    	
                    	mMap.setOnMapClickListener(new OnMapClickListener(){

							@Override
							public void onMapClick(LatLng arg0) {
								// TODO Auto-generated method stub
								Log.i(TAG,"map clicked");
							}
                    		
							
                    		
                    	});
                    	
//                    	mMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener(){
//
//							@Override
//							public boolean onMyLocationButtonClick() {								
//								Log.d(TAG,"onMyLocationButtonClick");
//								bMapWillFollow = true;
//								return false;
//							}														                    		
//                    	});
                    	
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

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		pListener = listener;
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		pListener = null;
	}

  	
	
	
	
}
