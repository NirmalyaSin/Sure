package com.surefiz.screens.profile.model.provider;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("message")
	private String message;

	@SerializedName("providerlist")
	private List<ProviderlistItem> providerlist;

	public String getMessage(){
		return message;
	}

	public List<ProviderlistItem> getProviderlist(){
		return providerlist;
	}
}