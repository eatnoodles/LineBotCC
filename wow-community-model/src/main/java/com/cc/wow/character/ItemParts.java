package com.cc.wow.character;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class ItemParts {

	private final String name;
	
	private final Integer itemLevel;
	
	/**
	 * 附魔
	 */
	private final Appearance appearance;
	
	public ItemParts(@JsonProperty("name") String name,
			@JsonProperty("itemLevel") Integer itemLevel,
			@JsonProperty("appearance") Appearance appearance){
		this.name = name;
		this.itemLevel = itemLevel;
		this.appearance = appearance;
	}
	
}
