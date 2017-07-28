/**
 * 
 */
package com.cc.wcl.rank;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class CharacterRankResponse {

	private final Long encounter;
	
	private final Integer clz;
	
	private final Integer spec;
	
	private final String guild;
	
	private final Long rank;
	
	private final Long outOf;
	
	private final Long duration;
	
	private final Date startTime;
	
	private final String reportID;
	
	private final Integer fightID;
	
	private final Integer difficulty;
	
	private final Integer size;
	
	private final Integer itemLevel;
	
	private final Long total;
	
	private final Boolean estimated;

	public CharacterRankResponse(
			@JsonProperty("encounter") Long encounter,
			@JsonProperty("class")Integer clz,
			@JsonProperty("spec")Integer spec,
			@JsonProperty("guild")String guild,
			@JsonProperty("rank")Long rank,
			@JsonProperty("outOf")Long outOf,
			@JsonProperty("duration")Long duration,
			@JsonProperty("startTime")Date startTime,
			@JsonProperty("reportID")String reportID,
			@JsonProperty("fightID")Integer fightID,
			@JsonProperty("difficulty")Integer difficulty,
			@JsonProperty("size")Integer size,
			@JsonProperty("itemLevel")Integer itemLevel,
			@JsonProperty("total")Long total,
			@JsonProperty("estimated")Boolean estimated) {
		this.encounter = encounter;
		this.clz = clz;
		this.spec = spec;
		this.guild = guild;
		this.rank = rank;
		this.outOf = outOf;
		this.duration = duration;
		this.startTime = startTime;
		this.reportID = reportID;
		this.fightID = fightID;
		this.difficulty = difficulty;
		this.size = size;
		this.itemLevel = itemLevel;
		this.total = total;
		this.estimated = estimated;
	}
	
}
