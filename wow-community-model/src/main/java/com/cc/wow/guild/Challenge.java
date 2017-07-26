/**
 * 
 */
package com.cc.wow.guild;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class Challenge {

	private final Realm realm;
	
	private final Map map;
	
	private final List<Group> groups;
	
	public Challenge(
            @JsonProperty("realm") Realm realm,
            @JsonProperty("map") Map map,
            @JsonProperty("groups") List<Group> groups
            ) {
        this.realm = realm;
        this.map =  map;
        this.groups = groups != null ? groups : Collections.emptyList();
    }
}
