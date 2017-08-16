package com.cc.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Caleb.Cheng
 *
 */
public enum IrolEventEnum {
	
	OPEN(1, ""),
	BATTLE(2, ""),
	FIGHT(3, ""),
	SKILL(4, ""),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, IrolEventEnum > map = new HashMap<>();

	static
	{
		for( IrolEventEnum eventEnum : IrolEventEnum.values() )
			map.put( eventEnum.getValue(), eventEnum );
	}
	
	private IrolEventEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static IrolEventEnum getEnumByValue( int value )
	{
		IrolEventEnum eventEnum = map.get( value );

		return eventEnum == null ? IrolEventEnum.NULL : eventEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
