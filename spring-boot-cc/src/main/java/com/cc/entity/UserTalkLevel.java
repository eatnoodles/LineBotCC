package com.cc.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.cc.entity.key.UserTalkLevelKey;

/**
 * @author Caleb Cheng
 *
 */
@Entity
@Table(name = "USER_TALK_LEVEL")
public class UserTalkLevel {
	
	public UserTalkLevel () {
		this.id = new UserTalkLevelKey();
	}
	
	public UserTalkLevel (String lineId, String talking) {
		this.id = new UserTalkLevelKey(lineId, talking);
	}

	@EmbeddedId
	private UserTalkLevelKey id;
	
	@NotNull
	@Column(name = "TALK_COUNT")
	private int talkCount;
	
	@Column(name = "LINE_ID")
	public String getLineId() {
		return this.id.getLineId();
	}
	
	public void setLineId(String lineId) {
		this.id.setLineId(lineId);
	}
	
	@Column(name = "TALKING")
	public String getTalking() {
		return this.id.getTalking();
	}
	
	public void setTalking(String talking) {
		this.id.setTalking(talking);
	}

	public int getTalkCount() {
		return talkCount;
	}

	public void setTalkCount(int talkCount) {
		this.talkCount = talkCount;
	}
	
}
