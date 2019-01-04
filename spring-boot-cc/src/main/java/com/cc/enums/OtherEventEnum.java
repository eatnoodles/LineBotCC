package com.cc.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Caleb.Cheng
 *
 */
public enum OtherEventEnum {
	
	ROLL(1, "擲骰"),
	GET_USER_ID(2, "取得userId"),
	LEAVE(3, "離開"),
	WHOAMI(4, "我是誰"),
	WCL_USER(5, "WCL"),
	IMG1(6, "><"),
	TALKING(7, "talk"),
	USER_ROLL_START(8, ""),
	USER_ROLL_END(9, ""),
	EMOJI(10, ""),
	PARROT(11, ""),
	FOOD(12, ""),
	SAVE_FOOD(13, ""),
	GAUSS(14, ""),
	SHORTENER(15, ""),
	LMGFTY(16, ""),
	SAD(17, ""),
	SAKI(18, ""),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, OtherEventEnum > map = new HashMap<>();

	static
	{
		for( OtherEventEnum eventEnum : OtherEventEnum.values() )
			map.put( eventEnum.getValue(), eventEnum );
	}
	
	private OtherEventEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static OtherEventEnum getEnumByValue( int value )
	{
		OtherEventEnum eventEnum = map.get( value );

		return eventEnum == null ? OtherEventEnum.NULL : eventEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
