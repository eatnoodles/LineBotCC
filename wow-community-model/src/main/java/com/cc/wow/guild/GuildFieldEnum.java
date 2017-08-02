/**
 * 
 */
package com.cc.wow.guild;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Caleb Cheng
 *
 */
public enum GuildFieldEnum {

	NEWS(4, "news"),
	CHALLENGE(5, "challenge"),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, GuildFieldEnum > map = new HashMap<>();

	static
	{
		for( GuildFieldEnum fieldEnum : GuildFieldEnum.values() )
			map.put( fieldEnum.getValue(), fieldEnum );
	}
	
	private GuildFieldEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static GuildFieldEnum getEnumByValue( int value )
	{
		GuildFieldEnum fieldEnum = map.get( value );

		return fieldEnum == null ? GuildFieldEnum.NULL : fieldEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
