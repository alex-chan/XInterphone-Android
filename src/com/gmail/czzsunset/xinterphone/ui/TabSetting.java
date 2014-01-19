package com.gmail.czzsunset.xinterphone.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.czzsunset.xinterphone.R;


public class TabSetting extends Fragment {
	
	private static final String TAG = "TabSetting";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");					
		
	    View v = inflater.inflate(R.layout.fragment_main_tabsetting, container, false);
	    
	    return v;
	}	
	
}
