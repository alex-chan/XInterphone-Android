package com.gmail.czzsunset.xinterphone.ui;






import com.gmail.czzsunset.xinterphone.R;
import com.gmail.czzsunset.xinterphone.FpaService;
import com.gmail.czzsunset.xinterphone.Protocol;
import com.google.android.gms.maps.model.LatLng;





import android.annotation.TargetApi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

public class MainActivity extends ActionBarActivity    {


	private static final String TAG = "MainActivity";

	int mCurTabIdx = -1;
	
	public int test = 1;

    int mTabBtns[] = {R.id.btn_my_map, R.id.btn_my_group, R.id.btn_my_setttings };
    int mTabText[] = { R.string.str_map, R.string.str_group, R.string.str_setting };
    int mTabItems[] = { R.id.item_my_map, R.id.item_my_group, R.id.item_my_settings };  
    
    String mTabTag[] = { "TAG_MY_MAP", "TAG_MY_GROUP", "TAG_SETTINGS"};
    

	LocalBroadcastManager mLbcManager ;

	
	private BroadcastReceiver protocolReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			Log.d(TAG, "receieved action"+action);
			
			if( Protocol.ACTION_UPDATE_PEER_LOCATION.equals(action)){
				// Update the location of peer 
				//Fragment fragment = getActionBar().get(0); // MapFragment is at index 0
				
				
		    	FragmentManager manager = getSupportFragmentManager();  
		    	Fragment fragment = manager.findFragmentByTag("TAG_MY_MAP");
		    	

				int groupId = intent.getIntExtra(Protocol.EXTRA_GROUP_ID, 0);
				int userId = intent.getIntExtra(Protocol.EXTRA_USER_ID, 0);
				long mills = intent.getLongExtra(Protocol.EXTRA_MILLIS, 0L);
				
				float lat = intent.getFloatExtra(Protocol.EXTRA_LATITUDE, 0f);
				float lng = intent.getFloatExtra(Protocol.EXTRA_LONGITUDE, 0f);
				float alt = intent.getFloatExtra(Protocol.EXTRA_ALTITUDE, 0f);
				float acc = intent.getFloatExtra(Protocol.EXTRA_ACCURACY, 0f);
				
				LatLng latlng = new LatLng(lat, lng);
				
//				fragment.addMarker(latlng, 1, userId, "UNDEFINED", "SNIP"	);
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		setupActionBar();

		registerServiceUpdate();
		
		selectTab(0);
		
		listenToTabButtons();
				
		startService();
		
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy(){
		mLbcManager.unregisterReceiver(protocolReceiver);
		super.onDestroy();

		System.exit(1);
		
	}
    /**
     * Backward-compatible version of {@link ActionBar#getThemedContext()} that
     * simply returns the {@link android.app.Activity} if
     * <code>getThemedContext</code> is unavailable.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public Context getActionBarThemedContextCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return getSupportActionBar().getThemedContext();
        } else {
            return this;
        }
    }	

	private void registerServiceUpdate(){
        IntentFilter  filter = new IntentFilter(Protocol.ACTION_UPDATE_PEER_LOCATION);
        
        mLbcManager = LocalBroadcastManager.getInstance(this);
        mLbcManager.registerReceiver(protocolReceiver , filter);	    
	}
	
	
	/** Setup ActionBar and Spinner
	 */
	private void setupActionBar(){
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

       

        
        
        /*
        
//        actionBar.add
        
        Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tabmap_menus, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);    
        
        */
	}

    
    public void listenToTabButtons(){
    	
    	for(int i=0;i<mTabBtns.length;i++){
    		
    		LinearLayout layout = (LinearLayout)findViewById(mTabItems[i]);
//    		ImageButton layout = (ImageButton)findViewById(btns[i]);
    		final int j = i;
    		
    		layout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					selectTab(j);
				}
			});
    		
			
    		 
    	}
    	
    }
    
    /**
     * Select Tab item in Main to show, hide the current showing tab item
     * @param index the tab item index need to show
     */
    public void selectTab(int index){
    	
    	if( mCurTabIdx != -1){
    		hideTab(mCurTabIdx);
    	}
    	
    	showTab(index);
    	mCurTabIdx = index;
    	
    }
    
    /**
     * Hide tab item
     * @param index 
     */
    private void hideTab(int index){
    	FragmentManager manager = getSupportFragmentManager();  
    	Fragment fragment = manager.findFragmentByTag(mTabTag[index]);
    	
    	
    	if(fragment !=null){
    		manager.beginTransaction().hide(fragment).commit();
    	}
    	
    }
    
    

    
    /**
     * Show tab item
     * @param index 
     */
    private void showTab(int index){
    	Log.d(TAG, "showTab:"+index);
    	
    	FragmentManager manager = getSupportFragmentManager();  
    	Fragment frag = manager.findFragmentByTag(mTabTag[index]);
    	
    	
    	if(frag !=null){
    		manager.beginTransaction()
    				.show(frag)
    				.commit();
    	}else{
    		
    		
    		Fragment fragment ;
    		
        	switch(index){
	    		
        		
	    		case 0:
	    			fragment = new TabMap();
	    			break;
	    			
	    		case 1:
	    			fragment = new TabGroup();
	    			break;
	    			
	    		case 2:
	    			fragment = new TabSetting();
	    			break;
	    			
	    		default:
	    			
	    			
	    			index = 0;
	    			return;
	   		}
    		
    		
    		
    		manager.beginTransaction()
    				.add(R.id.host, fragment, mTabTag[index])
    				.commit();
    		
    	}
    	
    }	
	
	
	
    public void startService(){
    	Log.d(TAG,"startService");
    	startService(new Intent(this,
                FpaService.class));    	
    }	

    public void stopService(){
    	Log.d(TAG, "stopService");
    	stopService(new Intent(this, FpaService.class));
    }
   
    
    @Override
    public boolean  onOptionsItemSelected(MenuItem item){
    	
    	if(item.getItemId() == R.id.action_exit){
    		// exit
//    		Intent exitIntent = new Intent( FpaService.ACTION_STOP_SERVICE );
//    		sendBroadcast(exitIntent);
    		stopService();
    		this.finish();
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }




}
