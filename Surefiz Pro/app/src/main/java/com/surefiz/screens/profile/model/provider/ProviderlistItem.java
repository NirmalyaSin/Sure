package com.surefiz.screens.profile.model.provider;

import com.google.gson.annotations.SerializedName;

public class ProviderlistItem{

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private String id;

	public String getName(){
		return name;
	}

	public String getId(){
		return id;
	}
}