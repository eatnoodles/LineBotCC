/**
 * 
 */
package com.cc.wow.character;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class Appearance {

	private final Long enchantDisplayInfoId;
	
	public Appearance(@JsonProperty("enchantDisplayInfoId") Long enchantDisplayInfoId){
		this.enchantDisplayInfoId = enchantDisplayInfoId; 
	}
}
