package com.cc.bean;

import com.cc.enums.IrolEventEnum;

/**
 * @author Caleb Cheng
 *
 */
public class IrolCommandBean extends CommandBean {
	
	private String irolName;

	private IrolEventEnum eventEnum;

	public IrolEventEnum getEventEnum() {
		return eventEnum;
	}

	public void setEventEnum(IrolEventEnum eventEnum) {
		this.eventEnum = eventEnum;
	}

	public String getIrolName() {
		return irolName;
	}

	public void setIrolName(String irolName) {
		this.irolName = irolName;
	}
}
