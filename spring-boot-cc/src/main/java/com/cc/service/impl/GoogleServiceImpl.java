package com.cc.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cc.google.client.GoogleApiService;
import com.cc.google.client.GoogleApiServiceBuilder;
import com.cc.google.client.GoogleClient;
import com.cc.google.client.GoogleClientImpl;
import com.cc.google.shortener.ShortenUrl;
import com.cc.service.IGoogleService;
import com.linecorp.bot.model.message.TextMessage;

@Component
public class GoogleServiceImpl implements IGoogleService {

	private static final Logger LOG = LoggerFactory.getLogger(GoogleServiceImpl.class);

	private GoogleClient googleClient;
	{
		GoogleApiService googleApiService = GoogleApiServiceBuilder.create(System.getenv("GoogleApiKey")).build();
		googleClient = new GoogleClientImpl(googleApiService);
	}
	
	@Override
	public TextMessage getShortenURL(String url) {
		try {
			ShortenUrl result = googleClient.getShortenURL(url).get();
			return new TextMessage(result.getId());
		} catch(Exception e) {
			return null;
		}
	}

}
