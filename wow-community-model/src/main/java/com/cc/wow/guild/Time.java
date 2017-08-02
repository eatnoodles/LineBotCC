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
public class Time {
	private final Long time;
    private final Integer hours;
    private final Integer minutes;
    private final Integer seconds;
    private final Integer milliseconds;
    private final Boolean isPositive;
    
	public Time(
			@JsonProperty("time") Long time,
			@JsonProperty("hours") Integer hours,
			@JsonProperty("minutes") Integer minutes,
			@JsonProperty("seconds") Integer seconds,
			@JsonProperty("milliseconds") Integer milliseconds,
			@JsonProperty("isPositive") Boolean isPositive
			) {
		this.time = time;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.milliseconds = milliseconds;
		this.isPositive = isPositive;
	}
    
}
