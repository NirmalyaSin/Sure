package com.surefiz.screens.profile.model.state;

import com.google.gson.annotations.SerializedName;

public class DataItem{

	@SerializedName("stateName")
	private String stateName;

	@SerializedName("stateID")
	private String stateID;

	public void setStateName(String stateName){
		this.stateName = stateName;
	}

	public String getStateName(){
		return stateName;
	}

	public void setStateID(String stateID){
		this.stateID = stateID;
	}

	public String getStateID(){
		return stateID;
	}
}