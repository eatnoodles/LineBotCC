package com.cc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Caleb Cheng
 *
 */
@Entity
@Table(name = "user_img_func")
public class UserImgFunc {

	@Id
	@Column(name = "LINE_ID")
	private String lineId;

	@Column(name = "status")
	private boolean status;

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
}
