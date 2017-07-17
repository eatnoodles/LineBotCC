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
public enum WowGuildEnum {

	NEWS(1, "news"),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, WowGuildEnum > map = new HashMap<>();

	static
	{
		for( WowGuildEnum fieldEnum : WowGuildEnum.values() )
			map.put( fieldEnum.getValue(), fieldEnum );
	}
	
	private WowGuildEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static WowGuildEnum getEnumByValue( int value )
	{
		WowGuildEnum fieldEnum = map.get( value );

		return fieldEnum == null ? WowGuildEnum.NULL : fieldEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
