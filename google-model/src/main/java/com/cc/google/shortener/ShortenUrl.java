package com.cc.google.shortener;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class ShortenUrl {

	private final String kind;
	
	private final String id;
	
	private final String longUrl;
	
	public ShortenUrl(@JsonProperty("kind") String kind,
			@JsonProperty("id") String id,
			@JsonProperty("longUrl") String longUrl) {
		this.id = id;
		this.kind = kind;
		this.longUrl = longUrl;
	}
}
