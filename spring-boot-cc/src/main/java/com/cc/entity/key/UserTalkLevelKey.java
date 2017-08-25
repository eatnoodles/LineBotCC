/**
 * 
 */
package com.cc.entity.key;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * @author Caleb Cheng
 *
 */
@SuppressWarnings("serial")
@Embeddable
public class UserTalkLevelKey implements Serializable {

	private String lineId;

	private String talking;
	
	public UserTalkLevelKey() {
		
	}

	public UserTalkLevelKey(String lineId, String talking) {
		this.lineId = lineId;
		this.talking = talking;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getTalking() {
		return talking;
	}

	public void setTalking(String talking) {
		this.talking = talking;
	}
}
