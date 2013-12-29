package com.gmail.czzsunset.xinterphone.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.czzsunset.xinterphone.R;
import com.gmail.czzsunset.xinterphone.App;
import com.gmail.czzsunset.xinterphone.Constants;
import com.gmail.czzsunset.xinterphone.model.Group;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Administrator
 * Intercom Preference Setting Fragment
 */

public class ICPrefFragment extends Fragment implements OnClickListener  {
    /**
     * 
     */
    public final static String TAG = "InterphoneSettingFragment";
    
    
    public final static int DEFAULT_ENCRYPTION_TYPE = 0;
    public final static int DEFAULT_UPDATE_INTERVAL_INDEX = 4;


    
    private  String[] mEncryptionTypes ;
    public static String[] mFreq;
    
    private String[] mUpdateIntervalString;
    public static int[] mUpdateIntervalValue;
     
    
    private Group mGroup ;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
//        setHasOptionsMenu(true);    
//        addPreferencesFromResource(R.xml.interpreferences);
        
        mEncryptionTypes = getResources().getStringArray(R.array.encrytion_array);      
        mGroup = ((App)getActivity().getApplication()).getGroup();
        
        Log.d(TAG, "mGroup:"+mGroup);
        initFrequence();
        initUpdateInterval();
        
    }
    
    private void initUpdateInterval(){
        mUpdateIntervalValue = getResources().getIntArray(R.array.update_interval_array_i);
        mUpdateIntervalString = getResources().getStringArray(R.array.update_interval_array);
    }
    
    private void initFrequence(){
        mFreq = getResources().getStringArray( R.array.interphone_freq_array);
        
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");                 
        
        View v = inflater.inflate(R.layout.fragment_interphone_setting, container, false);        
        
        

        
        
        LinearLayout layout1 = (LinearLayout) ( v.findViewById(R.id.lEncryptionType));      
        LinearLayout layout2 = (LinearLayout) ( v.findViewById(R.id.lUpdateInterval));
        LinearLayout layout3 = (LinearLayout) ( v.findViewById(R.id.lInterphoneFreq));
        
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);

        
        return v;
    } 
    
    @Override
    public void onViewCreated(View  v, Bundle savedState){
        super.onViewCreated(v, savedState);
        
        OnEncryptionTypeSelected(DEFAULT_ENCRYPTION_TYPE);
        OnUpdateIntervalSelected(DEFAULT_UPDATE_INTERVAL_INDEX);
        
        Random rand = new Random();
        int i = rand.nextInt(mFreq.length);
        
        OnFreqSelected( i );
        
    }
    
    
    
    @Override
    public void onClick(View v) {
        switch( v.getId() ){
            case R.id.lEncryptionType:
                                                       
                EncryptionPickerFrag frag1 = new EncryptionPickerFrag();
                frag1.show(
                        ( (ActionBarActivity)getActivity() ).getSupportFragmentManager(), 
                        "EncryptionPicker");                                               
                frag1.setFragment(ICPrefFragment.this);
                break;
                
            case R.id.lInterphoneFreq:
                
                FreqPickerFrag frag2 = new FreqPickerFrag();
                frag2.show(
                            ((ActionBarActivity)getActivity()).getSupportFragmentManager(),
                            "FreqPicker");
                frag2.setFragment(ICPrefFragment.this);        
                
                break;
                
            case R.id.lUpdateInterval:
                UpdateIntervalPickerFrag frag3 = new UpdateIntervalPickerFrag();
                frag3.show(
                        ( (ActionBarActivity)getActivity() ).getSupportFragmentManager(), 
                        "UpdateIntervalPicker");                                               
                frag3.setFragment(ICPrefFragment.this);                        
                
                break;
        }                
    }    
    
    
    
    public void OnEncryptionTypeSelected(int which){

        
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putInt(Constants.IC_ENCRY_TYPE, which);
//        editor.commit();
        
        mGroup.setEncryption(which);
        
        
        TextView tv = (TextView)getActivity().findViewById(R.id.tvEncryptionType);             
        tv.setText( mEncryptionTypes[which]);
                
    }    

    public void OnUpdateIntervalSelected(int which){

        
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putInt(Constants.IC_UPDATE_INTERVAL, mUpdateIntervalValue[which]);
//        editor.commit();     
        
        mGroup.setUpdateInterval(mUpdateIntervalValue[which]);
        
        TextView tv = (TextView)getActivity().findViewById(R.id.tvUpdateInterval);            
        tv.setText( mUpdateIntervalString[which]);
                
    }
        
    public void OnFreqSelected(int which){

        
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(Constants.IC_FREQ, mFreq[which]);
//        editor.commit();     
        
        mGroup.setFrequence(mFreq[which]);
        
        TextView tv = (TextView)getActivity().findViewById(R.id.tvInterphoneFreq );            
        tv.setText( mFreq[which]);
                
    }    
    
    
    

    public static class EncryptionPickerFrag extends DialogFragment{
        
        public ICPrefFragment frag;

        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.encryption_type)
                   .setItems(R.array.encrytion_array, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           
                          frag.OnEncryptionTypeSelected(which);                           
                            
                       }
                   })            
                   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
        
        
        public void setFragment(ICPrefFragment frag){
            this.frag = (ICPrefFragment)frag;
        }
  
    }    
        
    
    public static class FreqPickerFrag extends DialogFragment{
        
        public ICPrefFragment frag;

        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.interphone_freq)
                   .setItems(R.array.interphone_freq_array, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           
                          frag.OnFreqSelected(which);                           
                            
                       }
                   })            
                   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
        
        
        public void setFragment(ICPrefFragment frag){
            this.frag = (ICPrefFragment)frag;
        }
  
    }     
    
    public static class UpdateIntervalPickerFrag extends DialogFragment{
        
        public ICPrefFragment frag;

        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.location_update_interval)
                   .setItems(R.array.update_interval_array, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                           
                          frag.OnUpdateIntervalSelected(which);                           
                            
                       }
                   })            
                   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
        
        
        public void setFragment(ICPrefFragment frag){
            this.frag = (ICPrefFragment)frag;
        }
  
    }     
    
}
