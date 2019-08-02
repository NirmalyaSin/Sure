package com.surefiz.screens.boardcast.model;

import com.google.gson.annotations.SerializedName;

public class BroadcastItem{

	@SerializedName("dateTime")
	private String dateTime;

	@SerializedName("name")
	private String name;

	@SerializedName("message")
	private String message;

	@SerializedName("messageFrom")
	private String messageFrom;

	public void setDateTime(String dateTime){
		this.dateTime = dateTime;
	}

	public String getDateTime(){
		return dateTime;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setMessageFrom(String messageFrom){
		this.messageFrom = messageFrom;
	}

	public String getMessageFrom(){
		return messageFrom;
	}
}