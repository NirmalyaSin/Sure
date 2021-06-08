package com.surefiz.screens.bodycodition.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BodyItem implements Serializable {

	@SerializedName("selection")
	private boolean selection;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	public boolean isSelection() {
		return selection;
	}

	public void setSelection(boolean selection) {
		this.selection = selection;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}