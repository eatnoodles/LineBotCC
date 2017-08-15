package com.cc.bean;

import com.cc.enums.WoWEventEnum;

/**
 * @author Caleb Cheng
 *
 */
public class WoWCommandBean extends CommandBean {

private String name;
	
	private String realm;
	
	private String location;
	
	private String metric;
	
	private String mode;
	
	private WoWEventEnum eventEnum;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public WoWEventEnum getEventEnum() {
		return eventEnum;
	}

	public void setEventEnum(WoWEventEnum eventEnum) {
		this.eventEnum = eventEnum;
	}
	
}
