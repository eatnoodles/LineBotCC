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
public class CharacterProfileResponse {

	/**
	 * 最後更新時間
	 */
	private final Long lastModified;
	
	/**
	 * 名稱
	 */
	private final String name;
	
	/**
	 * server
	 */
	private final String realm;

	/**
	 * group
	 */
	private final String battlegroup;
	
	/**
	 * 職業
	 */
	private final Integer clz;
	
	/**
	 * 種族
	 */
	private final Integer race;
	
	private final Integer gender;
	
	/**
	 * 等級
	 */
	private final Integer level;
	
	/**
	 * 成就點數
	 */
	private final Long achievementPoints;
	
	private final String thumbnail;
	
	/**
	 * 天賦
	 */
	private final String calcClass;
	
	private final Integer faction;
	
	/**
	 * 榮譽擊殺
	 */
	private final Long totalHonorableKills;
	
	private final CharacterItemsResponse items;
	
	public CharacterProfileResponse(
            @JsonProperty("lastModified") Long lastModified,
            @JsonProperty("name") String name,
            @JsonProperty("realm") String realm,
            @JsonProperty("battlegroup") String battlegroup,
            @JsonProperty("class") Integer clz,
            @JsonProperty("race") Integer race,
            @JsonProperty("gender") Integer gender,
            @JsonProperty("level") Integer level,
            @JsonProperty("achievementPoints") Long achievementPoints,
            @JsonProperty("thumbnail") String thumbnail,
            @JsonProperty("calcClass") String calcClass,
            @JsonProperty("faction") Integer faction,
            @JsonProperty("totalHonorableKills") Long totalHonorableKills,
            @JsonProperty("items") CharacterItemsResponse items
            ) {
        this.lastModified = lastModified;
        this.name = name;
        this.realm = realm;
        this.battlegroup = battlegroup;
        
        this.clz = clz;
        this.race = race;
        this.gender = gender;
        this.level = level;
        this.achievementPoints = achievementPoints;
        this.thumbnail = thumbnail;
        this.calcClass = calcClass;
        this.faction = faction;
        this.totalHonorableKills = totalHonorableKills;
        this.items = items;
    }
}
