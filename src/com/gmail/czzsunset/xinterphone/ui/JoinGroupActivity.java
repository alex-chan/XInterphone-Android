package com.gmail.czzsunset.xinterphone.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.czzsunset.xinterphone.R;
import com.gmail.czzsunset.xinterphone.model.Group;
import com.gmail.czzsunset.xinterphone.net.NetOperation;


public class JoinGroupActivity extends ActionBarActivity {
    NetOperation mNetOp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join);
        
        mNetOp = new NetOperation(this);
        
        listenToJoinGroup();        
        setupActionBar();
    }
    
    private void toast(int resourceId){
        Toast.makeText(this, getResources().getString(resourceId), Toast.LENGTH_SHORT)
            .show();
    }
    
    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
            .show();
    }
    
    private void listenToJoinGroup(){
        Button btn = (Button)findViewById( R.id.btnDoJoinGroup);
        final EditText et = (EditText)findViewById( R.id.etGroupId);
        btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String groupIdStr = et.getText().toString();
                
                if( groupIdStr.length() == 0){
                    toast( R.string.input_noting );
                    return;
                }
                int groupId = Integer.parseInt(groupIdStr);
                joinGroup(groupId);                             
            }
        } );
    }
    
    private void setupActionBar(){
        
        final ActionBar actionBar = getSupportActionBar();
        
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.goback);
    }
    
    private void joinGroup(Integer groupId){
        new JoinGroupTask().execute(groupId);
    } 
    
    private void OnGroupJoined(Group groupInfo){
        if( groupInfo == null){
            // Fail to join the group, get the reason
            String lastError = mNetOp.getLastError();
            toast(lastError);
            
        }else{
            toast("Success join group "+ groupInfo.getName());
            
        }
    }    
    
 
        
    protected class JoinGroupTask extends AsyncTask<Integer, Void, Group>{

        @Override
        protected Group doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            return mNetOp.joinGroup(params[0]);            
        }
        
        @Override
        protected void onPostExecute(Group result) {
            OnGroupJoined(result);
        }
    }    
    
}
