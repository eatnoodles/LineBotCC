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
public enum WoWGuildEnum {

	NEWS(1, "news"),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, WoWGuildEnum > map = new HashMap<>();

	static
	{
		for( WoWGuildEnum fieldEnum : WoWGuildEnum.values() )
			map.put( fieldEnum.getValue(), fieldEnum );
	}
	
	private WoWGuildEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static WoWGuildEnum getEnumByValue( int value )
	{
		WoWGuildEnum fieldEnum = map.get( value );

		return fieldEnum == null ? WoWGuildEnum.NULL : fieldEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
