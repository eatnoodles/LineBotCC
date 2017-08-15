package com.cc.bean;

import com.cc.enums.WoWEventEnum;

/**
 * @author Caleb.Cheng
 *
 */
public abstract class CommandBean {
	
	private String errorMsg;
	
	protected String senderId;
	
	protected String userId;
	
	protected String command;

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
