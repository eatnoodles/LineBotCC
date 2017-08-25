package com.cc.entity.key;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.cc.entity.irol.Irol;

/**
 * @author Caleb Cheng
 *
 */
@SuppressWarnings("serial")
@Embeddable
public class MasterCardsKey implements Serializable {

	private String lineId;

	@ManyToOne
	@JoinColumn(name = "IROL_ID")
	private Irol irol;
	
	public MasterCardsKey() {
		
	}

	public MasterCardsKey(String lineId, Irol irol) {
		this.lineId = lineId;
		this.irol = irol;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public Irol getIrol() {
		return irol;
	}

	public void setIrol(Irol irol) {
		this.irol = irol;
	}
}
