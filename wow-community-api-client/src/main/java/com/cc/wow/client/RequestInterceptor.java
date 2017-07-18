package com.cc.wow.client;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 
 * @author Caleb Cheng
 *
 */
public class RequestInterceptor implements Interceptor {

	private final String apiKey;
	
	private final String locale;

    RequestInterceptor(String apiKey, String locale) {
        this.apiKey = apiKey;
        this.locale = locale;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
    	Request request = chain.request();
    	
        HttpUrl url = request.url().newBuilder()
        		.addQueryParameter("apiKey", apiKey)
        		.addQueryParameter("locale", locale)
        		.build();
        
        request = request.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
