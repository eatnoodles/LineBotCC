package com.cc.entity.irol;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Caleb Cheng
 *
 */
@Entity
@Table(name = "FIGHTING_IROL_STATUS")
public class FightingIrolStatus {
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "HP")
	private int hp;
	
	@ManyToOne
	@JoinColumn(name = "irol_id")
	private Irol irol;
	
	@Column(name = "ATK")
	private int atk;
	
	@Column(name = "maxhp")
	private int maxhp;
	
	@Column(name = "DEF")
	private int def;
	
	@Column(name = "SPEED")
	private int speed;
	
	/**
	 * 0:dead
	 * 1:normal
	 */
	@Column(name = "STATUS")
	private int status;
	
	public static final int STATUS_DEAD = 0;
	
	public static final int STATUS_NORMAL = 1;
	
	@OneToMany
	@JoinColumn(name = "fighting_irol_status_id")
	private List<FightingIrolBuffStatus> fightingIrolBuffStatusList;
	
	@OneToMany
	@JoinColumn(name = "fighting_irol_status_id")
	private List<FightingIrolDebuffStatus> fightingIrolDebuffStatusList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Irol getIrol() {
		return irol;
	}

	public void setIrol(Irol irol) {
		this.irol = irol;
	}

	public List<FightingIrolBuffStatus> getFightingIrolBuffStatusList() {
		return fightingIrolBuffStatusList;
	}

	public void setFightingIrolBuffStatusList(List<FightingIrolBuffStatus> fightingIrolBuffStatusList) {
		this.fightingIrolBuffStatusList = fightingIrolBuffStatusList;
	}

	public List<FightingIrolDebuffStatus> getFightingIrolDebuffStatusList() {
		return fightingIrolDebuffStatusList;
	}

	public void setFightingIrolDebuffStatusList(List<FightingIrolDebuffStatus> fightingIrolDebuffStatusList) {
		this.fightingIrolDebuffStatusList = fightingIrolDebuffStatusList;
	}

	public int getAtk() {
		return atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getMaxhp() {
		return maxhp;
	}

	public void setMaxhp(int maxhp) {
		this.maxhp = maxhp;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
