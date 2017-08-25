package com.cc.bean;

import com.cc.enums.OtherEventEnum;

/**
 * @author Caleb Cheng
 *
 */
public class OtherCommandBean extends CommandBean {
	
	private OtherEventEnum eventEnum;

	public OtherCommandBean(String command, String senderId, String userId) {
		this.command = command;
		this.senderId = senderId;
		this.userId = userId;
	}

	public OtherEventEnum getEventEnum() {
		return eventEnum;
	}

	public void setEventEnum(OtherEventEnum eventEnum) {
		this.eventEnum = eventEnum;
	}
	
}
