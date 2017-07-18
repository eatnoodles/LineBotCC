/**
 * 
 */
package com.cc.wow.boss;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class Boss {

	private final Long id;
	
	private final String name;
	
	private final String urlSlug;
	
	private final String description;
	
	private final Long zoneId;
	
	private final String availableInNormalMode;
	
	private final String availableInHeroicMode;
	
	private final Long health;
	
	private final Integer level;
	
	private final Integer heroicLevel;
	
	private final Integer heroicHealth;
	
	private final Long journalId;
	
	private final List<BossNPC> npcs;
	
	public Boss(@JsonProperty("id") Long id,
			@JsonProperty("name") String name,
			@JsonProperty("urlSlug") String urlSlug,
			@JsonProperty("description") String description,
			@JsonProperty("zoneId") Long zoneId,
			@JsonProperty("availableInNormalMode") String availableInNormalMode,
			@JsonProperty("availableInHeroicMode") String availableInHeroicMode,
			@JsonProperty("health") Long health,
			@JsonProperty("level") Integer level,
			@JsonProperty("heroicLevel") Integer heroicLevel,
			@JsonProperty("heroicHealth") Integer heroicHealth,
			@JsonProperty("journalId") Long journalId,
			@JsonProperty("npcs") List<BossNPC> npcs
			) {
		this.id = id;
		this.name = name;
		this.urlSlug = urlSlug;
		this.description = description;
		this.zoneId = zoneId;
		this.availableInNormalMode = availableInNormalMode;
		this.availableInHeroicMode = availableInHeroicMode;
		this.health = health;
		this.level = level;
		this.heroicLevel = heroicLevel;
		this.heroicHealth = heroicHealth;
		this.journalId = journalId;
		this.npcs = npcs != null ? npcs : Collections.emptyList();
	}
	
}
