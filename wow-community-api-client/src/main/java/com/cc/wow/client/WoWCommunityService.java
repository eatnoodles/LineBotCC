package com.cc.wow.client;

import com.cc.wow.character.CharacterProfileResponse;
import com.cc.wow.guild.GuildProfileResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * 
 * @author Caleb Cheng
 *
 */
public interface WoWCommunityService {
    
    /**
     * 
     * @see <a href="https://dev.battle.net/io-docs">//https://dev.battle.net/io-docs</a>
     */
    @Streaming
    @GET("character/{realm}/{characterName}")
    Call<CharacterProfileResponse> getCharacterByFields(@Path("realm") String realm, @Path("characterName") String characterName,
    		@Query(value="fields", encoded=true) String fields);

    /**
     * 
     * @see <a href="https://dev.battle.net/io-docs">//https://dev.battle.net/io-docs</a>
     */
    @Streaming
    @GET("guild/{realm}/{guildName}")
    Call<GuildProfileResponse> getGuildByFields(@Path("realm") String realm, @Path("guildName") String guildName,
    		@Query(value="fields", encoded=true) String fields);
}
