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
public class GuildProfileResponse {

	/**
	 * 最後更新時間
	 */
	private final Long lastModified;
	
	/**
	 * 公會名稱
	 */
	private final String name;
	
	/**
	 * server
	 */
	private final String realm;
	
	/**
	 * 群組
	 */
	private final String battlegroup;
	
	/**
	 * 公會等級
	 */
	private final Integer level;
	
	private final Integer side;

	/**
	 * 成就點數
	 */
	private final Long achievementPoints;
	
	private final List<Challenge> challenge;
	
	private final List<New> news;
	
	public GuildProfileResponse(
            @JsonProperty("lastModified") Long lastModified,
            @JsonProperty("name") String name,
            @JsonProperty("realm") String realm,
            @JsonProperty("battlegroup") String battlegroup,
            @JsonProperty("level") Integer level,
            @JsonProperty("side") Integer side,
            @JsonProperty("achievementPoints") Long achievementPoints,
            @JsonProperty("challenge") List<Challenge> challenge,
            @JsonProperty("news") List<New> news
            ) {
        this.lastModified = lastModified;
        this.name =  name;
        this.realm =  realm;
        this.battlegroup =  battlegroup;
        this.level =  level;
        this.side =  side;
        this.achievementPoints =  achievementPoints;
        this.challenge = challenge != null ? challenge : Collections.emptyList();
        this.news = news != null ? news : Collections.emptyList();
    }
}
