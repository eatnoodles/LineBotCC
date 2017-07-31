/**
 * 
 */
package com.cc.bean;

import com.cc.enums.WowEventEnum;

/**
 * @author Caleb.Cheng
 *
 */
public class WowCommandBean {
	
	private String name;
	
	private String realm;
	
	private String location;
	
	private String metric;
	
	private String mode;
	
	private WowEventEnum eventEnum;
	
	private String errorMsg;
	
	private boolean isWowCommand = true;

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

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public WowEventEnum getEventEnum() {
		return eventEnum;
	}

	public void setEventEnum(WowEventEnum eventEnum) {
		this.eventEnum = eventEnum;
	}

	public boolean isWowCommand() {
		return isWowCommand;
	}

	public void setWowCommand(boolean isWowCommand) {
		this.isWowCommand = isWowCommand;
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
}
