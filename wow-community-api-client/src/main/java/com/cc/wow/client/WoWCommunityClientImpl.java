package com.cc.wow.client;

import java.util.concurrent.CompletableFuture;

import com.cc.wow.character.CharacterFieldEnum;
import com.cc.wow.character.CharacterProfileResponse;
import com.cc.wow.guild.GuildFieldEnum;
import com.cc.wow.guild.GuildProfileResponse;

import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AllArgsConstructor
public class WoWCommunityClientImpl implements WoWCommunityClient {
	
    private final WoWCommunityService retrofitImpl;
	
	@Override
	public CompletableFuture<CharacterProfileResponse> getCharacterProfile(String realm, String characterName) {
		return toFuture(retrofitImpl.getCharacterByFields(realm, characterName, CharacterFieldEnum.PROFILE.getContext()));
	}
	
	@Override
	public CompletableFuture<CharacterProfileResponse> getCharacterItems(String realm, String characterName) {
		return toFuture(retrofitImpl.getCharacterByFields(realm, characterName, CharacterFieldEnum.ITEMS.getContext()));
	}
	
	@Override
	public CompletableFuture<GuildProfileResponse> getGuildChallenge(String realm, String characterName) {
		return toFuture(retrofitImpl.getGuildByFields(realm, characterName, GuildFieldEnum.CHALLENGE.getContext()));
	}
	
	@Override
	public CompletableFuture<GuildProfileResponse> getGuildNews(String realm, String characterName) {
		return toFuture(retrofitImpl.getGuildByFields(realm, characterName, GuildFieldEnum.NEWS.getContext()));
	}

	private static <T> CompletableFuture<T> toFuture(Call<T> callToWrap) {
        final CallbackAdaptor<T> completableFuture = new CallbackAdaptor<>();
        callToWrap.enqueue(completableFuture);
        return completableFuture;
    }
	
	static class CallbackAdaptor<T> extends CompletableFuture<T> implements Callback<T> {
        @Override
        public void onResponse(final Call<T> call, final Response<T> response) {
            if (response.isSuccessful()) {
                complete(response.body());
            }
        }

        @Override
        public void onFailure(final Call<T> call, final Throwable t) {
            completeExceptionally(
                    new Exception(t.getMessage()));
        }
    }
}
