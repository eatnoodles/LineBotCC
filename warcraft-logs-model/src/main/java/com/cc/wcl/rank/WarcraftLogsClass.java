/**
 * 
 */
package com.cc.wcl.rank;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class WarcraftLogsClass {

	private int id;
	
	private String name;
	
	private List<Spec> specs;

	public WarcraftLogsClass(@JsonProperty("id")int id,
				   @JsonProperty("name")String name,
				   @JsonProperty("specs")List<Spec> specs)
	{
		this.id = id;
		this.name = name;
		this.specs = specs;
	}
	
}
