package com.cc.entity.irol;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Caleb Cheng
 *
 */
@Entity
@Table(name = "Fighting_Irol_Buff_Status")
public class FightingIrolBuffStatus {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "buff_id")
	private Buff buff;
	
	@Column(name = "fighting_irol_status_id")
	private Long fightingIrolStatusId;
	
	@Column(name = "OVER_COUNT")
	private int overCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Buff getBuff() {
		return buff;
	}

	public void setBuff(Buff buff) {
		this.buff = buff;
	}

	public int getOverCount() {
		return overCount;
	}

	public void setOverCount(int overCount) {
		this.overCount = overCount;
	}

	public Long getFightingIrolStatusId() {
		return fightingIrolStatusId;
	}

	public void setFightingIrolStatusId(Long fightingIrolStatusId) {
		this.fightingIrolStatusId = fightingIrolStatusId;
	}
}
