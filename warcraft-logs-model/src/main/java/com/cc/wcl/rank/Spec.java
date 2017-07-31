/**
 * 
 */
package com.cc.wcl.rank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class Spec {

	private int id;
	
	private String name;

	public Spec(@JsonProperty("id")int id,
				@JsonProperty("name")String name)
	{
		this.id = id;
		this.name = name;
	}
	
}
