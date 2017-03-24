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
public enum WowProfileFieldEnum {

	ITEMS(7, "items"),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, WowProfileFieldEnum > map = new HashMap<>();

	static
	{
		for( WowProfileFieldEnum fieldEnum : WowProfileFieldEnum.values() )
			map.put( fieldEnum.getValue(), fieldEnum );
	}
	
	private WowProfileFieldEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static WowProfileFieldEnum getEnumByValue( int value )
	{
		WowProfileFieldEnum fieldEnum = map.get( value );

		return fieldEnum == null ? WowProfileFieldEnum.NULL : fieldEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
