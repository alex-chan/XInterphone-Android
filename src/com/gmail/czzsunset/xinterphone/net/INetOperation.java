package com.gmail.czzsunset.xinterphone.net;

import java.util.List;
import java.util.Map;

import com.gmail.czzsunset.xinterphone.model.Group;
import com.gmail.czzsunset.xinterphone.model.User;
import com.google.android.gms.maps.model.LatLng;

public interface INetOperation {
    
    
    /**
     * Create a group in server
     * @param name group name
     * @return group's global Id, 0 for fail
     */
    int  createGroup(String name);
    
    
    int createGroup(Group group);
    
        
    
    
    /**
     * Setup interphone's parameter of specific group. only can be used by group admin
     * 
     * @param groupId group's global id
     * @param gbw Bandwidth,0 for narrow bandwidth(12.5KHz), 1 for wide bandwidth(25K) 
     * @param tfv TX frequency:400.0000-470.0000 (must be multiple of 6.25K or 5K)
     * @param rfv RX frequency:400.0000-470.0000 (must be multiple of 6.25K or 5K)
     * @param rxcxcss Value of RX CTCSS/CDCSS : 00-121.
     *           00: No code,
     *           01-38: analog CTCSS, 
     *           39-121 digital analog 
     * @param sq Silence quality, the higher the better quality to retain noise
     *           0 : no noise retain
     *           1-8 :  
     * @param txcxcss Value of TX CTCSS/CDCSS : 00-121. 
     *           00: No code,
     *           01-38: analog CTCSS, 
     *           39-121 digital analog 
     */
    
    void setupGroupInterphoneParam(int groupId, boolean gbw, float tfv, float rfv, 
                                    int rxcxcss, int sq, int txcxcss);
    
    
    /**
     * Get interphone's parameters of a specific group from server
     * 
     * @param groupId group's global Id
     * @return A map of interphone's parameters, includes:<br/>
     *  
     *      groupLocalId: byte
     *          group's local Id used in a not wide region.
     *                  type byte is used in protocol to save space.<br/>
     *      gbw: Bandwidth,0 for narrow bandwidth(12.5KHz),
     *                   1 for wide bandwidth(25K)<br/>
     *      
     *      tfv TX frequency:400.0000-470.0000 (must be multiple of 6.25K or 5K)
     *      rfv RX frequency:400.0000-470.0000 (must be multiple of 6.25K or 5K)
     *      rxcxcss Value of RX CTCSS/CDCSS : 00-121.
     *           00: No code,
     *           01-38: analog CTCSS, 
     *           39-121 digital analog 
     *      sq Silence quality, the higher the better quality to retain noise
     *           0 : no noise retain
     *           1-8 :  
     *      txcxcss Value of TX CTCSS/CDCSS : 00-121. 
     *           00: No code,
     *           01-38: analog CTCSS, 
     *           39-121 digital analog 
     *  </ul>
     */
    Map getGroupInterphoneParam(int groupId);
    
    
    /**
     * Returns the nearby groups's list
     * @param lnglat The longitude and latitude of user
     * @return A list of Group instance
     */
    List<Group> getNearbyGroups(LatLng latlng);
    
    /**
     * Return the group members of specific group
     * @param groupId
     * @return A list of User instance
     */
    List<User> getGroupMembers(int groupId);
    
    /**
     * Join a specific group
     * @param groupId group's Id to join
     * @return true if succeed, otherwise false
     */
    Group joinGroup(int groupId);
    
    
    /**
     * Leave a specific group
     * @param groupId group's Id to leave
     * @return true if succeed, otherwise false
     */
    boolean leaveGroup(int groupId);
    
    
    
}
