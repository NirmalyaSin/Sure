package com.surefiz.screens.profile.model.provider;

import com.google.gson.annotations.SerializedName;

public class ProviderResponse{

	@SerializedName("data")
	private Data data;

	@SerializedName("status")
	private int status;

	public Data getData(){
		return data;
	}

	public int getStatus(){
		return status;
	}
}