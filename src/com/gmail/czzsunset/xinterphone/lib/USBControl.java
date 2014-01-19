/*
 * Copyright (C) 2012 Paul Bovbel, paul@bovbel.com
 * 
 * This file is part of the Mover-Bot robot platform (http://code.google.com/p/mover-bot/)
 * 
 * Mover-Bot is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code. If not, see http://www.gnu.org/licenses/
 */
package com.gmail.czzsunset.xinterphone.lib;


import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.gmail.czzsunset.xinterphone.Constants;
// Android 2.3.4-3.1
//import com.android.future.usb.UsbAccessory;
//import com.android.future.usb.UsbManager;
// Above android 3.1

/** Configures a USB accessory and its input/output streams.
 * 
 * Call this.send to sent a byte array to the accessory
 * Override onReceive to process incoming bytes from accessory
 */

public abstract class USBControl extends Thread{

	// The permission action
	private static final String ACTION_USB_PERMISSION = Constants.PACKAGE_PREFIX + "action.USB_PERMISSION";
	private static final String TAG = "USBControl"; 

	// An instance of accessory and manager
	private UsbAccessory mAccessory;
	private UsbManager mManager;
	private Context context;
	private Handler UIHandler;
	private Handler controlSender;
	private Thread controlListener; 
	boolean connected = false;
	private ParcelFileDescriptor mFileDescriptor;
	private FileInputStream input;
	private FileOutputStream output;

	//Receiver for connect/disconnect events
	BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
				    UsbAccessory accessory = getAccessory(intent);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						Log.i(TAG, "Permission granted");
						openAccessory(accessory);
					} else {
						Log.e(TAG,"Permission denied");
					}

				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
			 
				UsbAccessory accessory = getAccessory(intent);
                if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
					
				}
			}
 
		}
	};

	private UsbAccessory getAccessory(Intent intent){
	   
//        return UsbManager.getAccessory(intent);
	    return (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

	}

    private UsbManager getUsbManager(Context context){

//       return UsbManager.getInstance(context); // 2.3.4-3.1
       return (UsbManager) context.getSystemService(Context.USB_SERVICE);        
    }

	//Configures the usb connection
	public USBControl(Context main, Handler ui)
	{
		super("USBControlSender");
		UIHandler = ui;
		context = main;

		
        mManager = getUsbManager(context);
		
		UsbAccessory[] accessoryList = mManager.getAccessoryList();
		PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0,
				new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		
		Log.d(TAG, "registerReceiver");
		context.registerReceiver(mUsbReceiver, filter);

		UsbAccessory mAccessory = (accessoryList == null ? null : accessoryList[0]);
		if (mAccessory != null) {

			/*while(!mManager.hasPermission(mAccessory)){
				mManager.requestPermission(mAccessory, mPermissionIntent);
			}*/
			if( !mManager.hasPermission(mAccessory)){
				Log.i(TAG, "Don't have permission, request permission");
				mManager.requestPermission(mAccessory, mPermissionIntent);
			}else{
				Log.i(TAG, "already has permission");
				openAccessory(mAccessory);
			}

		}

	}

	//Send byte array over connection
	public void send(byte[] command){
		if (controlSender != null){
			Message msg = controlSender.obtainMessage();
			msg.obj = command;
			controlSender.sendMessage(msg);
		}
	}

	//Receive byte array over connection
	private void receive(final byte[] msg){

		//pass to ui thread for processing
		UIHandler.post(new Runnable() {
			public void run() {
				onReceive(msg);
			}
		});
	}

	public abstract void onReceive(byte[] msg);

	public abstract void onNotify(String msg);

	public abstract void onConnected();

	public abstract void onDisconnected();

	
	@Override
	public void run() {
		
		Log.d(TAG, "starting linstener and sender");
		controlListener = new Thread(new Runnable() {
			
			public void run() {
				Log.d(TAG, "listener running");
				int i=0;
				boolean running = true;

				while(running){
					byte[] msg = new byte[120];
					try{
						if( (i% 2000) == 0){ 
							Log.d(TAG,   "listener running..");
							i++;
						}
						//Handle incoming messages
						while (running && input != null && input.read(msg) != -1 ){
							
							if( msg[0] != -1 ){ 
								// This is a trick. that arduino sends to android to prevent read block.
								Log.i(TAG, "received data");
								Log.i(TAG, Arrays.toString(msg));
								receive(msg);
								Thread.sleep(20);	
							}
							
						}
					}catch (final Exception e){			
						Log.e(TAG, "listener failed:"+e.toString() );
						UIHandler.post(new Runnable() {
							public void run() {
								Log.d(TAG, "USB Receive failed");
								onNotify("USB Receive Failed " + e.toString() + "\n");
								closeAccessory();
								
							}
						});
						running = false;
					}
				}
			}
		});
		controlListener.setDaemon(true);
		controlListener.setName("USBCommandListener");
		controlListener.start();	
		
		//Sends messages to usb accessory
		Looper.prepare();
		controlSender = new Handler() {
			public void handleMessage(Message msg) {
				try{
					output.write((byte[])msg.obj);
				}catch(final Exception e){
					UIHandler.post(new Runnable() {
						public void run() {
							onNotify("USB Send Failed " + e.toString() + "\n");
						}
					});
					controlSender.getLooper().quit();
				}						
			}
		};
		Looper.loop();
	}


	// Sets up filestreams
	private void openAccessory(UsbAccessory accessory) {
		Log.d(TAG, "opening accessory:"+accessory.toString() );
		mAccessory = accessory;
		mFileDescriptor = mManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			input = new FileInputStream(fd);
			output = new FileOutputStream(fd);
			Log.d(TAG, "accessory opened");
						
			this.start();
			onConnected();
			
		}else{
			Log.e(TAG, "Open accessory failed");
		}
		
		
		
		

	}

	// Cleans up accessory
	public void closeAccessory() {

		Log.d(TAG, "close accessory");
		//halt i/o
		if( controlSender != null){
			controlSender.getLooper().quit();
		}
		if( controlListener != null){
			Log.d(TAG, "interrupt constrolListener");
			controlListener.interrupt();			
		}

		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
			Log.e(TAG, "Close mFileDescriptor failed:"+e.toString() );
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
			
			input =  null;
			output = null;
		}

		onDisconnected();
	}

	//Removes the usb receiver
	public void destroyReceiver() {
		Log.d(TAG, "destroyReceiver");
		context.unregisterReceiver(mUsbReceiver);
	}

}
