package com.gmail.czzsunset.xinterphone.ui;

import com.gmail.czzsunset.xinterphone.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SimplePrefActivity extends Activity implements OnSharedPreferenceChangeListener{
	
	
	public static class SimpleFragment extends PreferenceFragment{
		
		@Override
		public void onCreate(Bundle savedInstatnceState){
			super.onCreate(savedInstatnceState);
			
			addPreferencesFromResource(R.xml.preference);
			
			SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
			Preference pref = findPreference(KEY_PREF_INTERPHONE_FREQ);
			pref.setSummary(sp.getString(KEY_PREF_INTERPHONE_FREQ, "400.0000") + "MHz" );
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
    
    
    private static final String KEY_PREF_INTERPHONE_FREQ = "interphone_freq";
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_INTERPHONE_FREQ)) {
   
            Preference connectionPref =   ((PreferenceFragment)
            							  	getFragmentManager().findFragmentById(android.R.id.content))
            							  .getPreferenceManager() 	            							  
            							  .findPreference(key);            						    
            		
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, "")+"MHz");
           
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
