package com.cc.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Caleb.Cheng
 *
 */
public enum WoWEventEnum {
	
	PROFILE(1, "取得角色基本資料"),
	IMG(2, "取得角色大頭照"),
	CHARACTER_ITEM(3, "角色裝備資訊"),
	CHECK_ENCHANTS(4, "看看這個傢伙有沒附魔的裝"),
	WCL(5, "WCL"),
	MAPPING_A(6, "儲存角色資訊"),
	HELP(995, "幫助"),
	TEST(99, "測試用"),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, WoWEventEnum > map = new HashMap<>();

	static
	{
		for( WoWEventEnum eventEnum : WoWEventEnum.values() )
			map.put( eventEnum.getValue(), eventEnum );
	}
	
	private WoWEventEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static WoWEventEnum getEnumByValue( int value )
	{
		WoWEventEnum eventEnum = map.get( value );

		return eventEnum == null ? WoWEventEnum.NULL : eventEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
