package com.surefiz.screens.users.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data{

	@SerializedName("userList")
	private List<UserListItem> userList;

	@SerializedName("SubUserAddStatus")
	private int subUserAddStatus;

	@SerializedName("message")
	private String message;

	public void setUserList(List<UserListItem> userList){
		this.userList = userList;
	}

	public List<UserListItem> getUserList(){
		return userList;
	}

	public void setSubUserAddStatus(int subUserAddStatus){
		this.subUserAddStatus = subUserAddStatus;
	}

	public int getSubUserAddStatus(){
		return subUserAddStatus;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}