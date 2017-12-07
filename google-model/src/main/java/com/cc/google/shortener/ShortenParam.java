package com.cc.google.shortener;

import lombok.Value;

@Value
public class ShortenParam {

	private String longUrl;
	
	public ShortenParam(String longUrl) {
		this.longUrl = longUrl;
	}
}
