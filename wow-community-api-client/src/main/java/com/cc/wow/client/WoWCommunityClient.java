/**
 * 
 */
package com.cc.wow.client;

import java.util.concurrent.CompletableFuture;

import com.cc.wow.character.CharacterProfileResponse;
import com.cc.wow.guild.GuildProfileResponse;

/**
 * @author Caleb Cheng
 *
 */
public interface WoWCommunityClient {
	
	/**
     * The Character Profile API is the primary way to access character information. 
     * This Character Profile API can be used to fetch a single character at a time through an HTTP GET 
     *  request to a URL describing the character profile resource. By default, a basic dataset will be returned and with each request and zero or more 
     *  additional fields can be retrieved. 
     * To access this API, craft a resource URL pointing to the character who's information is to be retrieved.
     *
     * @see <a href="https://dev.battle.net/io-docs">//https://dev.battle.net/io-docs</a>
     */
    CompletableFuture<CharacterProfileResponse> getCharacterProfile(String realm, String characterName);
    
    CompletableFuture<CharacterProfileResponse> getCharacterItems(String realm, String characterName);
    
    CompletableFuture<GuildProfileResponse> getGuildChallenge(String realm, String guildName);
    
    CompletableFuture<GuildProfileResponse> getGuildNews(String realm, String guildName);
}
