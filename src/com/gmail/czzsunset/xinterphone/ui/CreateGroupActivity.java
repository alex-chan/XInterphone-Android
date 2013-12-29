package com.gmail.czzsunset.xinterphone.ui;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.czzsunset.xinterphone.R;
import com.gmail.czzsunset.xinterphone.App;
import com.gmail.czzsunset.xinterphone.Constants;
import com.gmail.czzsunset.xinterphone.model.Group;
import com.gmail.czzsunset.xinterphone.net.NetOperation;

import java.util.prefs.Preferences;

public class CreateGroupActivity extends ActionBarActivity {
    
    
    private NetOperation mNetOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);
        
//        mNetOp = new NetOperation(this);
        
        listenOnCreateGroup();        
        setupActionBar();    
        setupFragment();
        
      
        mNetOp = new NetOperation(this);
        
    }
    
    private void setupFragment(){
        FragmentManager manager = getSupportFragmentManager();  
        Fragment fragment =  new ICPrefFragment();
        manager.beginTransaction()
        .add(R.id.layout_interphone_setting, fragment, ICPrefFragment.TAG)
        .commit();
    }
    
    private void toast(int resourceId){
        Toast.makeText(this, getResources().getString(resourceId), Toast.LENGTH_SHORT)
            .show();
    }
    
    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
            .show();
    }
    
    private void listenOnCreateGroup(){
        Button btn = (Button)findViewById( R.id.btnDoJoinGroup);
        final EditText et = (EditText)findViewById( R.id.etGroupId);        
        btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String groupName = et.getText().toString();
                if( groupName.length() == 0){
                    toast( R.string.input_noting );
                    return;
                }
                createGroup(groupName);
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
    
    private void createGroup(String name){
        Group group = ((App)this.getApplication()).getGroup();
        group.setName(name);
        new CreateGroupTask().execute(group);
    } 
    
    private void OnGroupCreated(Integer newGroupId){
        if( newGroupId != 0){
            toast("Success:"+ newGroupId);
            
            ((App)this.getApplication()).getGroup().setId(newGroupId);
            
            
        }else{

            String lastError = mNetOp.getLastError();
            toast(lastError);                               
        }
    }    
        
    protected class CreateGroupTask extends AsyncTask<Group, Void, Integer>{

        @Override
        protected Integer doInBackground(Group... params) {
            // TODO Auto-generated method stub
//            return mNetOp.createGroup(params[0]);
            return mNetOp.createGroup(params[0]);
        }
        
        

        @Override
        protected void onPostExecute(Integer result) {
            OnGroupCreated(result);
        }
    }    
    

    
        
}
