/**
 * 
 */
package com.cc.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Caleb.Cheng
 *
 */
public enum WoWClassEnum {

	DEATH_KNIGHT(6, "死亡騎士"),
	DEMON_HUNTER(12, "惡魔獵人"),
	DRUID(11, "德魯伊"),
	HUNTER(3, "獵人"),
	Mage(8, "法師"),
	MONK(10, "僧侶"),
	PALADIN(2, "聖騎士"),
	PRIEST(5, "牧師"),
	ROGUE(4, "盜賊"),
	SHAMAN(7, "薩滿"),
	WARLOCK(9, "術士"),
	WARRIOR(1, "戰士"),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, WoWClassEnum > map = new HashMap<>();

	static
	{
		for( WoWClassEnum classEnum : WoWClassEnum.values() )
			map.put( classEnum.getValue(), classEnum );
	}
	
	private WoWClassEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static WoWClassEnum getEnumByValue( int value )
	{
		WoWClassEnum classEnum = map.get( value );

		return classEnum == null ? WoWClassEnum.NULL : classEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
	
}
