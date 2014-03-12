package com.gmail.czzsunset.xinterphone.model;



public class SimpleUser{
	public int iUUID ;
	public int userCode;
	public double latitude;
	public double longitude;
	public double altitude;
	public long timestamp;
	public boolean isMySelf;
	
	public SimpleUser Creator(){
		return new SimpleUser();
	}
	
	public SimpleUser lat(double lat){
		this.latitude =lat;
		return this;
	}
	
}