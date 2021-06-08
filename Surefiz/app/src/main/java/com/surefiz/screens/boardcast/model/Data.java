package com.surefiz.screens.boardcast.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("broadcast")
	private List<BroadcastItem> broadcast;

	@SerializedName("message")
	private String message;

	public void setBroadcast(List<BroadcastItem> broadcast){
		this.broadcast = broadcast;
	}

	public List<BroadcastItem> getBroadcast(){
		return broadcast;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}