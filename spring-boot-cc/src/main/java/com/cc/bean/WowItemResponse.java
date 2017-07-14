package com.cc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WowItemResponse extends BaseWOWResponse {

	private String id;
	
	private String name;
	
	private String itemLevel;

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("itemLevel")
	public String getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(String itemLevel) {
		this.itemLevel = itemLevel;
	}
	
	
}
