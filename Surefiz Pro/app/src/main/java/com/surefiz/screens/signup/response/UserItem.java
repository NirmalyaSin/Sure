package com.surefiz.screens.signup.response;

import com.google.gson.annotations.SerializedName;

public class UserItem{

	@SerializedName("user_email")
	private String userEmail;

	@SerializedName("user_LastLogin")
	private String userLastLogin;

	@SerializedName("imageName")
	private Object imageName;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("scaleUserId")
	private String scaleUserId;

	@SerializedName("user_name")
	private String userName;

	@SerializedName("count")
	private String count;

	@SerializedName("user_mac")
	private String userMac;

	@SerializedName("user_permission")
	private String userPermission;

	@SerializedName("user_photo")
	private String userPhoto;

	@SerializedName("regtype")
	private String regtype;

	public void setUserEmail(String userEmail){
		this.userEmail = userEmail;
	}

	public String getUserEmail(){
		return userEmail;
	}

	public void setUserLastLogin(String userLastLogin){
		this.userLastLogin = userLastLogin;
	}

	public String getUserLastLogin(){
		return userLastLogin;
	}

	public void setImageName(Object imageName){
		this.imageName = imageName;
	}

	public Object getImageName(){
		return imageName;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setScaleUserId(String scaleUserId){
		this.scaleUserId = scaleUserId;
	}

	public String getScaleUserId(){
		return scaleUserId;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getUserName(){
		return userName;
	}

	public void setCount(String count){
		this.count = count;
	}

	public String getCount(){
		return count;
	}

	public void setUserMac(String userMac){
		this.userMac = userMac;
	}

	public String getUserMac(){
		return userMac;
	}

	public void setUserPermission(String userPermission){
		this.userPermission = userPermission;
	}

	public String getUserPermission(){
		return userPermission;
	}

	public void setUserPhoto(String userPhoto){
		this.userPhoto = userPhoto;
	}

	public String getUserPhoto(){
		return userPhoto;
	}

	public void setRegtype(String regtype){
		this.regtype = regtype;
	}

	public String getRegtype(){
		return regtype;
	}
}