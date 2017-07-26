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
public class Spec {
	
	private final String name;
	private final String role;
	private final String backgroundImage;
	private final String icon;
	private final String description;
	private final Integer order;
	
	public Spec(
			@JsonProperty("name") String name, 
			@JsonProperty("role") String role, 
			@JsonProperty("backgroundImage") String backgroundImage, 
			@JsonProperty("icon") String icon, 
			@JsonProperty("description") String description, 
			@JsonProperty("order") Integer order
			) {
		super();
		this.name = name;
		this.role = role;
		this.backgroundImage = backgroundImage;
		this.icon = icon;
		this.description = description;
		this.order = order;
	}
	
	
}
