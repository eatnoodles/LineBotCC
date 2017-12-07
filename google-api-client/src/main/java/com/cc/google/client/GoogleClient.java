/**
 * 
 */
package com.cc.google.client;

import java.util.concurrent.CompletableFuture;

import com.cc.google.shortener.ShortenUrl;

/**
 * @author Caleb Cheng
 *
 */
public interface GoogleClient {
	
	/**
     * The Google URL Shortener API allows you to shorten URLs just as you would on goo.gl.
     *
     * @see <a href="https://developers.google.com/url-shortener/v1/getting_started</a>
     */
    CompletableFuture<ShortenUrl> getShortenURL(String url);
}
