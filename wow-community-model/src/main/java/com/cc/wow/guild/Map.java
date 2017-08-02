/**
 * 
 */
package com.cc.wow.guild;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class Map {
	
	private final Long id;
	
    private final String name;
 
    public Map(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name
            ) {
        this.id = id;
        this.name = name;
    }
}
