package com.gmail.czzsunset.xinterphone.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.database.Cursor;
import com.gmail.czzsunset.xinterphone.R;
import com.gmail.czzsunset.xinterphone.lib.DatabaseHelper;
import com.gmail.czzsunset.xinterphone.lib.DatabaseHelper.MatesTable;
import com.gmail.czzsunset.xinterphone.lib.DatabaseHelper.TraceTable;
import com.gmail.czzsunset.xinterphone.model.User;
import com.gmail.czzsunset.xinterphone.net.NetOperation;

import java.util.List;

public class TabGroup extends ListFragment implements OnMenuItemClickListener ,
//                                                    OnQueryTextListener, 
//                                                    OnCloseListener,
                                                    LoaderManager.LoaderCallbacks<Cursor> { 
    private static final String TAG = "TabGroup";
    
    // Menu identifiers
    static final int POPULATE_ID = Menu.FIRST;
    static final int CLEAR_ID = Menu.FIRST+1;    

    
    AsyncTask<Integer, Void, String> mUpdateGroupTask;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);    
        
        
    }
    

    
    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter mAdapter;
    
    
    
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    
        

        
        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("No members");
    
        // We have a menu item to show in action bar.
//        setHasOptionsMenu(true);
    
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2, null,
                new String[] { MatesTable.NAME, TraceTable.LATITUDE},
                new int[] { android.R.id.text1, android.R.id.text2 }, 0);
        setListAdapter(mAdapter);
    
        // Start out with a progress indicator.
        setListShown(false);
    
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null,  this);
    }
    
    
    @Override 
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching.
//        MenuItem item = menu.add("Search");
//        item.setIcon(android.R.drawable.ic_menu_search);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
//                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//        mSearchView = new MySearchView(getActivity());
//        mSearchView.setOnQueryTextListener(this);
//        mSearchView.setOnCloseListener(this);
//        mSearchView.setIconifiedByDefault(true);
//        item.setActionView(mSearchView);
        
//        
//        final ActionBar actionBar = ( (ActionBarActivity)getActivity() ).getSupportActionBar();  
//        menu.add(Menu.NONE, POPULATE_ID, 0, "Populate")
//            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        menu.add(Menu.NONE, CLEAR_ID, 0, "Clear")
//            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
        inflater.inflate(R.menu.tabgroup, menu);
        super.onCreateOptionsMenu(menu, inflater);
        
    }
    

    
    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Log.i("FragmentComplexList", "Item clicked: " + id);
    }

    
    static final String[] MEMBERS_PROJECTION = new String[] {
        MatesTable.TABLE_NAME+ "." + MatesTable._ID,
        MatesTable.NAME,        
        TraceTable.TABLE_NAME + "." + TraceTable.LATITUDE
    };
    
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        
        return new CursorLoader(getActivity(), 
                Uri.withAppendedPath(TraceTable.CONTENT_URI, "latest"),
                MEMBERS_PROJECTION,
                null,
                null,
                null);        

    }
    

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
//        mAdapter.swapCursor(data);
        mAdapter.changeCursor(data);
        
    
        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }
    
    

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.changeCursor(null);    
    }
        
    
    public void showPopup(){
//        PopupMenu popup = new PopupMenu(getActivity(), v );
        PopupMenu popup = new PopupMenu( (MainActivity)getActivity(), 
                getActivity().findViewById(R.id.action_group_actions));
        
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.tabgroup_more, popup.getMenu());        
//        popup.inflate(R.menu.tabgroup_more);
        popup.show();
    }
    

    
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch(item.getItemId()){
            case R.id.action_group_actions:
                showPopup();
                break;
        }

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem arg0) {
        // TODO Auto-generated method stub
        boolean ret = false;
        switch(arg0.getItemId()){
            case R.id.groupaction_creategroup:
                
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                startActivity( intent);                
                ret = true;
                break;
                
            case R.id.groupaction_updategroup:                
                
                final TabGroup thistab = this;

                final NetOperation netOp = new NetOperation(this.getActivity());
                
                if (mUpdateGroupTask != null) {
                    mUpdateGroupTask.cancel(false);
                }
                mUpdateGroupTask = new AsyncTask<Integer, Void, String>(){
                    @Override
                    protected String doInBackground(Integer... groupId) {                        
                        List<User> members = netOp.getGroupMembers(groupId[0]);
                        
                        if( members.size() == 0){
                            // Error happens 
                            return  netOp.getLastError();                            
                        }
//                        if(isCancelled() ){
//                            break;
//                        }

                        DatabaseHelper dbHelper = new DatabaseHelper( getActivity());
                        
                        dbHelper.appendMates(members);
                                                
                        return "updated " + members.size() + " mebmers";
                        
                        
                    }
                    @Override
                    protected void onPostExecute(String msg) {
                        
//                        CharSequence text = "Updated "+users.size()+ " members";
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT)
                            .show();
                        getLoaderManager().restartLoader(0, null, thistab);
                                             
                    }                    
                };
                mUpdateGroupTask.execute(355);
                
                ret = true;
                break;
                
            case R.id.groupaction_joingroup:
                startActivity(new Intent(getActivity(), JoinGroupActivity.class) );
                ret = true;
                break;
                
            case R.id.groupaction_exitgroup:
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }


}
