package com.cc.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Caleb.Cheng
 *
 */
public enum WowEventEnum {
	
	PROFILE(1, "取得角色基本資料"),
	IMG(2, "取得角色大頭照"),
	ITEM(3, "角色裝備資訊"),
	TEST(99, "測試用"),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, WowEventEnum > map = new HashMap<>();

	static
	{
		for( WowEventEnum eventEnum : WowEventEnum.values() )
			map.put( eventEnum.getValue(), eventEnum );
	}
	
	private WowEventEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static WowEventEnum getEnumByValue( int value )
	{
		WowEventEnum eventEnum = map.get( value );

		return eventEnum == null ? WowEventEnum.NULL : eventEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
