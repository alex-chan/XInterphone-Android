package com.gmail.czzsunset.xinterphone.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.gmail.czzsunset.xinterphone.R;

public class SimplePrefActivity extends Activity implements OnSharedPreferenceChangeListener{
	
	
	public static final String TAG = "SimplePrefActivity";

    public static final String KEY_PREF_INTERPHONE_FREQ 	= "pref_interphone_freq";
    public static final String KEY_PREF_GROUP_ID			= "pref_group_id";
    public static final String KEY_PREF_MY_CODE 			= "pref_my_code";
    public static final String KEY_PREF_UPDATE_INTERVAL 	= "pref_broadcast_interval";	
	
	public static class SimpleFragment extends PreferenceFragment{
		
		@Override
		public void onCreate(Bundle savedInstatnceState){
			super.onCreate(savedInstatnceState);
			
			addPreferencesFromResource(R.xml.preference);
			
			SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
			Preference pref = findPreference(KEY_PREF_INTERPHONE_FREQ);
			pref.setSummary(sp.getString(KEY_PREF_INTERPHONE_FREQ, "400.0000") + "MHz" );
			
			pref = findPreference(KEY_PREF_GROUP_ID);
			pref.setSummary("#" + sp.getString(KEY_PREF_GROUP_ID,"0"));
			
			pref = findPreference(KEY_PREF_MY_CODE);
			pref.setSummary("#" + sp.getString(KEY_PREF_MY_CODE,"0"));
			
			pref = findPreference(KEY_PREF_UPDATE_INTERVAL);
			pref.setSummary(sp.getString(KEY_PREF_UPDATE_INTERVAL,"5") + "min");
			
			
			
		}
	}
	
	private SimpleFragment mPrefFragment = new SimpleFragment();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, mPrefFragment)
                .commit();
    }
    
    

    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	
    	Log.i(TAG,"onSharedPreferenceChanged");
    	
    	PreferenceManager prefMgnr =  ((PreferenceFragment)
					getFragmentManager().findFragmentById(android.R.id.content))
					.getPreferenceManager();    	
    	Preference connectionPref =  prefMgnr.findPreference(key);
    	
    	Log.d(TAG, "key:"+key);
    	
    	String val = sharedPreferences.getString(key, "");
    	
    	if(key.equals(KEY_PREF_INTERPHONE_FREQ)){
    	    // Set summary to be the user-description for the selected value
    		
            connectionPref.setSummary(sharedPreferences.getString(key, "")+"MHz");

    	}else if(key.equals(KEY_PREF_UPDATE_INTERVAL)){
    		
    		int interval = Integer.valueOf(val);
    		if(interval == 0){
    			Toast.makeText(this, "Should be larger than 0", Toast.LENGTH_LONG).show();        		    			    			
//    			SharedPreferences.Editor editor =  sharedPreferences.edit();
//        		editor.putInt(key, 3);        		
//        		connectionPref.setDefaultValue(sharedPreferences.getInt(key, 5));    			
    			return;
    		}
    		
    		connectionPref.setSummary(interval + "min");
    		
           
        }else if(key.equals(KEY_PREF_GROUP_ID)){
        	int interval = Integer.valueOf(val);
        	connectionPref.setSummary("#" + interval);
        	
        }else if(key.equals(KEY_PREF_MY_CODE)){
        	int interval = Integer.valueOf(val);
        	connectionPref.setSummary("#" + interval);
        	
        }
    }    
    
    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        getPreferenceScreen().getSharedPreferences()
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    
}
