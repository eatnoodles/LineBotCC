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
public enum WoWRaceEnum {

	HUMAN(1, "人類"),
	ORC(2, "獸人"),
	DWARF(3, "矮人"),
	NIGHT_ELF(4, "夜精靈"),
	UNDEAD(5, "不死族"),
	TAUREN(6, "牛頭人"),
	GNOME(7, "地精"),
	TROLL(8, "食人妖"),
	GOBLIN(9, "哥布林"),
	BLOOD_ELF(10, "血精靈"),
	DRAENEI(11, "德萊尼"),
	WORGEN(22, "狼人"),
	PANDAREN_NEUTRAL(24, "中立熊貓人"),
	PANDAREN_ALLIANCE(25, "聯盟的熊貓人"),
	PANDAREN_HORDE(26, "部落的熊貓人"),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, WoWRaceEnum > map = new HashMap<>();

	static
	{
		for( WoWRaceEnum raceEnum : WoWRaceEnum.values() )
			map.put( raceEnum.getValue(), raceEnum );
	}
	
	private WoWRaceEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static WoWRaceEnum getEnumByValue( int value )
	{
		WoWRaceEnum raceEnum = map.get( value );

		return raceEnum == null ? WoWRaceEnum.NULL : raceEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
