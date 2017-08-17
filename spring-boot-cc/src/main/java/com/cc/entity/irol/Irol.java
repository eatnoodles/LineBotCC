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
@Table(name = "IROL")
public class Irol {
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "NAME")
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "SKILL_1")
	private Skill skill1;
	
	@ManyToOne
	@JoinColumn(name = "SKILL_2")
	private Skill skill2;
	
	/**
	 * 1: fire
	 * 2: water
	 * 3: wind
	 * 4: holy
	 * 5: showdown
	 */
	@Column(name = "ATTRIBUTE")
	private int attribute;
	
	public static final int ATTR_FIRE = 1;
	
	public static final int ATTR_WATER = 2;
	
	public static final int ATTR_WIND = 3;
	
	public static final int ATTR_HOLY = 4;
	
	public static final int ATTR_SHOWDOWN = 5;
	
	@Column(name = "HP")
	private int hp;
	
	@Column(name = "ATK")
	private int atk;
	
	@Column(name = "DEF")
	private int def;
	
	@Column(name = "SPEED")
	private int speed;

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

	public Skill getSkill1() {
		return skill1;
	}

	public void setSkill1(Skill skill1) {
		this.skill1 = skill1;
	}

	public Skill getSkill2() {
		return skill2;
	}

	public void setSkill2(Skill skill2) {
		this.skill2 = skill2;
	}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
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

}
