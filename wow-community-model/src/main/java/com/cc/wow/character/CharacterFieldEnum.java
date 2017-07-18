/**
 * 
 */
package com.cc.wow.character;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Caleb Cheng
 *
 */
public enum CharacterFieldEnum {

	ITEMS(7, "items"),
	PROFILE(1, StringUtils.EMPTY),
	NULL(0, StringUtils.EMPTY);
	
	private int value;
	
	private String context;
	
	private static final Map< Integer, CharacterFieldEnum > map = new HashMap<>();

	static
	{
		for( CharacterFieldEnum fieldEnum : CharacterFieldEnum.values() )
			map.put( fieldEnum.getValue(), fieldEnum );
	}
	
	private CharacterFieldEnum(int value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static CharacterFieldEnum getEnumByValue( int value )
	{
		CharacterFieldEnum fieldEnum = map.get( value );

		return fieldEnum == null ? CharacterFieldEnum.NULL : fieldEnum;
	}

	public int getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
