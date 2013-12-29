package com.gmail.czzsunset.xinterphone.net;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gmail.czzsunset.xinterphone.R;
import com.gmail.czzsunset.xinterphone.App;
import com.gmail.czzsunset.xinterphone.Constants;
import com.gmail.czzsunset.xinterphone.model.Group;
import com.gmail.czzsunset.xinterphone.model.User;
import com.gmail.czzsunset.xinterphone.model.User.Sex;
import com.gmail.czzsunset.xinterphone.model.User.Status;
import com.gmail.czzsunset.xinterphone.ui.CreateGroupActivity;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class NetOperation implements INetOperation{
    
    
    private static final String TAG = "NetOperation";
    ConnectivityManager mConnMgr ;
    Context mContext ;
    

    private String mLastError = "";


    
   public NetOperation(Context context){
        
        mContext = context;        
        mConnMgr = (ConnectivityManager)    
                    mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
   
   public String getLastError(){
       return mLastError;
   }
   
   private String getString(int resourceId){
       return mContext.getResources().getString(resourceId);
   }
   
   private boolean isNetworkOk(){
        NetworkInfo networkInfo = mConnMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
             return true;
             
        }else {
            mLastError =  mContext.getResources()
                            .getString( R.string.network_unavailable);
            return false;
        }
    }
    
    private String readIt(InputStream is) throws IOException{
        
       ByteArrayOutputStream out = new ByteArrayOutputStream();
       String content;
       int read = -1;
       
       while( (read=is.read()) != -1 ){
           out.write( (byte)read);
       }
        
       
       byte[] data = out.toByteArray();
       out.close();
       
       content = new String( data);

       return content;
       
    }
    
    private String doHttpMethod(HashMap<String,String> params, String method) throws IOException{
        String proto = Constants.PROTOCOL_HTTP;
        String host = Constants.SERVER_HOST ;
        int port = Constants.SERVER_PORT ;        
        String charset = Constants.CHARSET ;
        
        App app = (App)((Activity)mContext).getApplication();
        String uuid = app.getMe().getUuid();
        params.put("uuid", uuid); // my uuid        
        params.put("v", "1");
        
        StringBuilder httpParams = new StringBuilder() ;
        
        httpParams.append( Constants.HTTP_API_PATH + "?");
        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while( it.hasNext() ){
            Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();

            String para = String.format(
                    "%s=%s&", 
                    URLEncoder.encode(pairs.getKey(), charset), 
                    URLEncoder.encode(pairs.getValue(), charset));            
            httpParams.append(para);                     
           
        }
        
        String newParams = new String(httpParams.deleteCharAt(httpParams.length()-1));
        
        
        URL url = new URL(proto, host, port, newParams);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setConnectTimeout(Constants.HTTP_CONNECT_TIMEOUT);
        conn.setRequestMethod(method);
        String content = readIt( conn.getInputStream());
        
        return content;
        
        
    }
    
    private String httpGet(HashMap<String,String> params) throws IOException{
        return doHttpMethod(params, "GET");
    }
    
    private JSONObject getJSON(HashMap<String,String> params) throws JSONException, IOException{
        String content = httpGet(params);
        return new JSONObject(content);        
    }
    
    private void post(){
        
    }
    
    @Override
    public int createGroup(Group group){
        
        HashMap<String,String> param = new HashMap<String,String>();
        param.put("method", "createGroup");
        param.put("name", group.getName());
        param.put("encryption", String.valueOf(group.getEncrytpion()) ) ;
        param.put("frequence", group.getFrequence() ) ;
        param.put("updateInterval", String.valueOf(group.getUpdateInterval()));
        

        
        JSONObject ret;
        
        int res;
        try {
            ret = getJSON(param);
            
            if( ret.getInt("retCode") != 0){
                // Error occurs
                mLastError = ret.getString("errMsg");
                res = 0;
            }else{
                // Create group success, return the newly created group ID
                res = ret.getInt("retVal");            
            }                           
            
        } catch (JSONException e) {
            e.printStackTrace();
            mLastError = e.getMessage();
            res = 0;
            
        } catch (IOException e) {
            e.printStackTrace();
            mLastError = e.getMessage();
            res = 0;
        }
        
        return res;
        
    }
    
    @Override
    public int createGroup(String name) {
        // TODO Auto-generated method stub
        return 0;
    }    
    


    @Override
    public void setupGroupInterphoneParam(int groupId, boolean gbw, float tfv, float rfv,
            int rxcxcss, int sq, int txcxcss) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Map getGroupInterphoneParam(int groupId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Group> getNearbyGroups(LatLng latlng) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<User> getGroupMembers(int groupId) {

        HashMap<String,String> param = new HashMap<String,String>();
        param.put("method", "getGroupMembers");
        param.put("groupId", String.valueOf(groupId) ) ;
        
        
        JSONObject ret;
        
        List<User> members = new ArrayList<User>() ;
        try {
            ret = getJSON(param);
            
            if( ret.getInt("retCode") != 0){
                // Error occurs
                mLastError = ret.getString("errMsg");                
            }else{
                // Create group success, return the newly created group ID
                JSONArray userArray = ret.getJSONArray("retVal");
                
                for(int i=0;i<userArray.length();i++){
                    JSONObject usrObj = userArray.getJSONObject(i);
                    
                    User user = new User();
                    user.setId(usrObj.getInt("id"));
                    user.setLocalId((byte)usrObj.getInt("local_id"));
                    user.setHeadImgUrl(usrObj.getString("head_url_img"));
                    user.setName(usrObj.getString("name"));                    
                    user.setUuid(usrObj.getString("uuid") );
                    user.setSex(Sex.fromString(usrObj.getString("sex")));
                    user.setStatus(Status.fromInteger(usrObj.getInt("status")));

                    members.add(user);
                }                
                               
            }                           
            
        } catch (JSONException e) {
            e.printStackTrace();
            mLastError = e.getMessage();            
            
        } catch (IOException e) {
            e.printStackTrace();
            
            mLastError = e.getLocalizedMessage();            
        }
        
        return members;     
    }

    @Override
    public Group joinGroup(int groupId) {
        
        HashMap<String,String> param = new HashMap<String,String>();
        param.put("method", "joinGroup");
        param.put("groupId", String.valueOf(groupId) ) ;
        
        
        JSONObject ret;
        
        Group res = null;
        try {
            ret = getJSON(param);
            
            if( ret.getInt("retCode") != 0){
                // Error occurs
                mLastError = ret.getString("errMsg");                
            }else{
                // Create group success, return the newly created group ID
                res = new Group( ret.getJSONObject("retVal") );
                               
            }                           
            
        } catch (JSONException e) {
            e.printStackTrace();
            mLastError = e.getMessage();            
            
        } catch (IOException e) {
            e.printStackTrace();
            mLastError = e.getMessage();            
        }
        
        return res;        
                
    }

    @Override
    public boolean leaveGroup(int groupId) {
        // TODO Auto-generated method stub
        return false;
    }


    
}
