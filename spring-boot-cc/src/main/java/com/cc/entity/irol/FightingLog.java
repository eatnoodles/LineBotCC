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
@Table(name = "FIGHTING_LOG")
public class FightingLog {
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "LINE_ID")
	private String lineId;
	
	@ManyToOne
	@JoinColumn(name = "monster_status_id")
	private FightingMonsterStatus fightingMonsterStatus;
	
	@ManyToOne
	@JoinColumn(name = "irol_status_id")
	private FightingIrolStatus fightingIrolStatus;
	
	@Column(name = "STATUS")
	private int status;
	
	@Column(name = "LAST_DTTM")
	private String lastDttm;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public FightingMonsterStatus getFightingMonsterStatus() {
		return fightingMonsterStatus;
	}

	public void setFightingMonsterStatus(FightingMonsterStatus fightingMonsterStatus) {
		this.fightingMonsterStatus = fightingMonsterStatus;
	}

	public FightingIrolStatus getFightingIrolStatus() {
		return fightingIrolStatus;
	}

	public void setFightingIrolStatus(FightingIrolStatus fightingIrolStatus) {
		this.fightingIrolStatus = fightingIrolStatus;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLastDttm() {
		return lastDttm;
	}

	public void setLastDttm(String lastDttm) {
		this.lastDttm = lastDttm;
	}
}
