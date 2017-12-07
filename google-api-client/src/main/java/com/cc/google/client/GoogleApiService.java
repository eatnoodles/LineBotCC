package com.cc.google.client;

import com.cc.google.shortener.ShortenUrl;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

/**
 * 
 * @author Caleb Cheng
 *
 */
public interface GoogleApiService {
    
    /**
     * 
     * @see <a href="https://developers.google.com/url-shortener/v1/getting_started">//https://developers.google.com/url-shortener/v1/getting_started</a>
     */
    @Streaming
    @POST("url")
    Call<ShortenUrl> getShortenURL(String url);
}
