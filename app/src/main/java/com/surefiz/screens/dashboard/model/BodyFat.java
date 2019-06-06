package com.surefiz.screens.dashboard.model;

import com.google.gson.annotations.SerializedName;

public class BodyFat{

	@SerializedName("colourCode")
	private String colourCode;

	@SerializedName("status")
	private String status;

	public void setColourCode(String colourCode){
		this.colourCode = colourCode;
	}

	public String getColourCode(){
		return colourCode;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

}