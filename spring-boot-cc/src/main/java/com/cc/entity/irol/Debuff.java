package com.cc.entity.irol;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Caleb Cheng
 *
 */
@Entity
@Table(name = "DEBUFF")
public class Debuff {
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "DEBUFF_COUNT")
	private int debuffCount;
	
	@Column(name = "DAMAGE")
	private int damage;
	
	@Column(name = "ATK")
	private int atk;
	
	@Column(name = "DEF")
	private int def;
	
	@Column(name = "SPEED")
	private int speed;
	
	@Column(name = "MAXHP")
	private int maxHp;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAtk() {
		return atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
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

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public int getDebuffCount() {
		return debuffCount;
	}

	public void setDebuffCount(int debuffCount) {
		this.debuffCount = debuffCount;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
}
