/**
 * 
 */
package com.cc.wow.guild;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class New {
	
	private final String type;
	private final String character;
	private final Date timestamp;
	private final Long itemId;
	private final String context;
	private final List<Long> bonusLists;
	
	public New(
			@JsonProperty("type") String type,
			@JsonProperty("character") String character,
			@JsonProperty("timestamp") Date timestamp,
			@JsonProperty("itemId") Long itemId,
			@JsonProperty("context") String context,
			@JsonProperty("bonusLists") List<Long> bonusLists
			) {
		this.type = type;
		this.character = character;
		this.timestamp = timestamp;
		this.itemId = itemId;
		this.context = context;
		this.bonusLists = bonusLists != null ? bonusLists : Collections.emptyList();
	}
	
	
}
