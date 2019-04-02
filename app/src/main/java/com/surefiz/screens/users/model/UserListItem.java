package com.surefiz.screens.users.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserListItem implements Serializable {

	@SerializedName("userWeight")
	private String userWeight;

	@SerializedName("scaleUserId")
	private int scaleUserId;

	@SerializedName("“isUserHaveCompleteInfo”")
	private int isUserHaveCompleteInfo;

	@SerializedName("userEmail")
	private String userEmail;

	@SerializedName("userName")
	private String userName;

	@SerializedName("serverUserId")
	private int serverUserId;

	public void setUserWeight(String userWeight){
		this.userWeight = userWeight;
	}

	public String getUserWeight(){
		return userWeight;
	}

	public void setScaleUserId(int scaleUserId){
		this.scaleUserId = scaleUserId;
	}

	public int getScaleUserId(){
		return scaleUserId;
	}

	public void setIsUserHaveCompleteInfo(int isUserHaveCompleteInfo){
		this.isUserHaveCompleteInfo = isUserHaveCompleteInfo;
	}

	public int getIsUserHaveCompleteInfo(){
		return isUserHaveCompleteInfo;
	}

	public void setUserEmail(String userEmail){
		this.userEmail = userEmail;
	}

	public String getUserEmail(){
		return userEmail;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setServerUserId(int serverUserId){
		this.serverUserId = serverUserId;
	}

	public int getServerUserId(){
		return serverUserId;
	}
}