package com.cc.google.client;

import com.cc.google.shortener.ShortenParam;
import com.cc.google.shortener.ShortenUrl;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
    @POST("url")
    Call<ShortenUrl> getShortenURL(@Body ShortenParam param);
}
