package com.gmail.czzsunset.xinterphone.model;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Group {
    
    public int mId; // global data in server
    public int mLocalId; // local group id used in local area to save data in protocol
    public String mImgUrl;
    public int mMemberCount;
    
    public LatLng mCurApproximatePos ;
    
    public String mName;
    public int mEncryption;
    public String mFrequence;
    public int mUpdateInterval;
    
        
    
    public Group() {
        // TODO Auto-generated constructor stub
    }    
    
    public Group(JSONObject jsonGroup) throws JSONException {
        // Initialize a group object using JSONObject 
        setId(jsonGroup.getInt("id") );           
        setName(jsonGroup.getString("name"));
        setFrequence(jsonGroup.getString("frequence") );        
        setUpdateInterval( jsonGroup.getInt("updateInterval") );        
        setEncryption( jsonGroup.getInt("encryption") );
        
    }



    
    public void setParams(String name, int  encryption, String frequence){
        setName(name);
        setEncryption(encryption);
        setFrequence(frequence);        
    }
    
    
    public void setId(int id){
        mId = id;
    }
    public int getId(){
        return mId;
    }
    
    public void setUpdateInterval(int updateInterval){
        mUpdateInterval = updateInterval;
    }
    public int getUpdateInterval(){
        return mUpdateInterval;
    }
    
    public void setName(String name){
        mName = name;
    }
    public String getName(){
        return mName;
    }
    
    public void setEncryption(int encryption){
        mEncryption = encryption;
    }
    public int getEncrytpion(){
        return mEncryption;
    }
    
    public void setFrequence(String frequence){
        mFrequence = frequence;
    }
    public String getFrequence(){
        return mFrequence;
    }
      
    
    

}
