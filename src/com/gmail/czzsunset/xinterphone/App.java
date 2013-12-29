package com.gmail.czzsunset.xinterphone;

import android.app.Application;

import com.gmail.czzsunset.xinterphone.model.Group;
import com.gmail.czzsunset.xinterphone.model.User;

public class App extends Application {

    private Group group = null;
    private User me = null;
    
    public Group getGroup(){
        return group;
    }
    public void setGroup(Group group){
        this.group = group;
    }
    
    public User getMe(){
        return me;
    }
    
    public void setMe(User me){
        this.me = me;
    }
    
    
    @Override
    public void onCreate(){        
        initSingletons();        
        super.onCreate();
    }
    
    private void initSingletons(){
        if( group == null){
            setGroup( new Group() );            
        }
        if( me == null){
            String id = Util.getUuid(this);
            setMe( new User(id) );
        }
    }
}
