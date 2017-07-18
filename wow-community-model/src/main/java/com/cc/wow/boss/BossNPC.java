/**
 * 
 */
package com.cc.wow.boss;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class BossNPC {

	private final Long id;
	
	private final String name;
	
	private final String urlSlug;
	
	public BossNPC(@JsonProperty("id") Long id,
			@JsonProperty("name") String name,
			@JsonProperty("urlSlug") String urlSlug
			) {
		this.id = id;
		this.name = name;
		this.urlSlug = urlSlug;
	}
}
