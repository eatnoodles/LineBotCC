package com.cc.wcl.client;


import java.util.List;
import com.cc.wcl.rank.CharacterRankResponse;
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
public interface WarcraftLogsService {
    
    /**
     * 
     * @see <a href="https://www.warcraftlogs.com/v1/docs</a>
     */
    @Streaming
    @GET("rankings/character/{characterName}/{serverName}/{serverRegion}")
    Call<List<CharacterRankResponse>> getRankingsByCharacter(@Path("characterName") String characterName, 
    													  @Path("serverName") String serverName,
    													  @Path("serverRegion") String serverRegion,
													  	  @Query(value="metric", encoded=true) String metric);

}
