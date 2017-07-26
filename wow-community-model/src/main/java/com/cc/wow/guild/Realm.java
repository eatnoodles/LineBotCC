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
public class Realm {
	
	private final String name;
	private final String slug;
	private final String battlegroup;
	private final String locale;
	private final String timezone;
	private final List<String> connected_realms;
	
	public Realm(
            @JsonProperty("name") String name,
            @JsonProperty("slug") String slug,
            @JsonProperty("battlegroup") String battlegroup,
            @JsonProperty("locale") String locale,
            @JsonProperty("timezone") String timezone,
            @JsonProperty("connected_realms") List<String> connected_realms
            ) {
        this.name = name;
        this.slug = slug;
        this.battlegroup = battlegroup;
        this.locale = locale;
        this.timezone = timezone;
        this.connected_realms = connected_realms != null ? connected_realms : Collections.emptyList();
    }
}
