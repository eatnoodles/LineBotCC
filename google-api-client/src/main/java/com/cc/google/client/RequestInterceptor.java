package com.cc.google.client;

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

    RequestInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
    	Request request = chain.request();
    	
        HttpUrl url = request.url().newBuilder()
        		.addQueryParameter("key", apiKey)
        		.build();
        
        request = request.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
