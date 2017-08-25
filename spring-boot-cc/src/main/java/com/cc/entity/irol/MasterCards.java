package com.cc.entity.irol;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.cc.entity.key.MasterCardsKey;

/**
 * @author Caleb Cheng
 *
 */
@Entity
@Table(name = "MASTER_CARDS")
public class MasterCards {

	@EmbeddedId
	private MasterCardsKey id;
	
	public MasterCards () {
		this.id = new MasterCardsKey();
	}
	
	public MasterCards (String lineId, Irol irol) {
		this.id = new MasterCardsKey(lineId, irol);
	}

	@Column(name = "LINE_ID")
	public String getLineId() {
		return this.id.getLineId();
	}
	
	public void setLineId(String lineId) {
		this.id.setLineId(lineId);
	}
	
	public Irol getIrol() {
		return this.id.getIrol();
	}
	
	public void setIrol(Irol irol) {
		this.id.setIrol(irol);
	}
	
}
