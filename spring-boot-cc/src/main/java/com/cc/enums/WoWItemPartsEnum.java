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
public enum WoWItemPartsEnum {

	HEAD("head", "頭部"),
	NECK("neck", "項鍊"),
	SHOULDER("shoulder", "肩膀"),
	BACK("back", "披風"),
	CHEST("chest", "胸部"),
	WRIST("wrist", "護腕"),
	HANDS("hands", "手套"),
	WAIST("waist", "腰帶"),
	LEGS("legs", "護腿"),
	FEET("feet", "靴子"),
	FINGER1("finger1", "戒指1"),
	FINGER2("finger2", "戒指2"),
	TRINKET1("trinket1", "飾品1"),
	TRINKET2("trinket2", "飾品2"),
	MAIN_HAND("mainHand", "主手"),
	OFF_HAND("offHand", "副手"),
	NULL(StringUtils.EMPTY, StringUtils.EMPTY);
	
	private String value;
	
	private String context;
	
	private static final Map< String, WoWItemPartsEnum > map = new HashMap<>();

	static
	{
		for( WoWItemPartsEnum itemPartsEnum : WoWItemPartsEnum.values() )
			map.put( itemPartsEnum.getValue(), itemPartsEnum );
	}
	
	private WoWItemPartsEnum(String value, String context){
		this.value = value;
		this.context = context;
	}
	
	public static WoWItemPartsEnum getEnumByValue( String value )
	{
		WoWItemPartsEnum itemPartsEnum = map.get( value );

		return itemPartsEnum == null ? WoWItemPartsEnum.NULL : itemPartsEnum;
	}

	public String getValue() {
		return value;
	}

	public String getContext() {
		return context;
	}
}
