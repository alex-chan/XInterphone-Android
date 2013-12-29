package com.gmail.czzsunset.xinterphone.ui;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gmail.czzsunset.xinterphone.R;

public class GroupActionActivity extends ActionBarActivity implements OnClickListener {
    
    
    private int btnIds[] = { R.id.btnCreateGroup, R.id.btnJoinGroup, R.id.btnExitGroup}; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_actions);
        
        
        listenToGroupAction();
        
        setupActionBar();
    }    
    
    private void listenToGroupAction(){
        
        for(int btnId : btnIds){
            Button btn = (Button)findViewById( btnId);
            btn.setOnClickListener(this);
        }
    }
    
    @Override
    public void onClick(View v){
       int btnId = v.getId();
       
       if( btnId == R.id.btnCreateGroup){
           startActivity(new Intent(this, CreateGroupActivity.class) );
       }else if( btnId == R.id.btnJoinGroup ){
           startActivity(new Intent(this, JoinGroupActivity.class) );
           
           
           
       }else if( btnId == R.id.btnExitGroup){
           
       }else if( btnId == R.id.btnUpdateGroup){
           
           
           
       }

    }
    
    
               
    
    private void setupActionBar(){
        
        final ActionBar actionBar = getSupportActionBar();
        
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.goback);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_groupaction);
    }    
    
}
