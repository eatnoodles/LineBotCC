/**
 * 
 */
package com.cc.wcl.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.cc.wcl.rank.CharacterRankResponse;


/**
 * @author Caleb Cheng
 *
 */
public interface WarcraftLogsClient {
	
    CompletableFuture<List<CharacterRankResponse>> getRankingsByCharacter(String characterName, String serverName, String serverRegion, String metric);
    
}
