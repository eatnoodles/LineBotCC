package com.cc.bean;

import com.cc.enums.IrolEventEnum;

/**
 * @author Caleb Cheng
 *
 */
public class IrolCommandBean extends CommandBean {
	
	private String irolName;
	
	private Long irolId;
	
	private Long monsterId;
	
	private Long skillId;

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

	public Long getIrolId() {
		return irolId;
	}

	public void setIrolId(Long irolId) {
		this.irolId = irolId;
	}

	public Long getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(Long monsterId) {
		this.monsterId = monsterId;
	}

	public Long getSkillId() {
		return skillId;
	}

	public void setSkillId(Long skillId) {
		this.skillId = skillId;
	}
}
