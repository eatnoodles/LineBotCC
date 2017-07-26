/**
 * 
 */
package com.cc.wow.guild;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class Group {

	private final Integer ranking;
	private final Time time;
	private final String date;
	private final String faction;
	private final Boolean isRecurring;
	
	private final List<Member> members;

	public Group(
			@JsonProperty("ranking") Integer ranking,
			@JsonProperty("time") Time time,
			@JsonProperty("date") String date,
			@JsonProperty("faction") String faction,
			@JsonProperty("isRecurring") Boolean isRecurring,
			@JsonProperty("members") List<Member> members
			) {
		this.ranking = ranking;
		this.time = time;
		this.date = date;
		this.faction = faction;
		this.isRecurring = isRecurring;
		this.members = members;
	}
	
	
}
