package com.cc.wow.client;

import java.util.concurrent.CompletableFuture;

import com.cc.wow.character.CharacterFieldEnum;
import com.cc.wow.character.CharacterProfileResponse;

import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AllArgsConstructor
public class WoWCommunityClientImpl implements WoWCommunityClient {
	
//	private static final ExceptionConverter EXCEPTION_CONVERTER = new ExceptionConverter();
    private final WoWCommunityService retrofitImpl;
	
	@Override
	public CompletableFuture<CharacterProfileResponse> getCharacterProfile(String realm, String characterName) {
		return toFuture(retrofitImpl.getCharacterByFields(realm, characterName, CharacterFieldEnum.PROFILE.getContext()));
	}
	
	@Override
	public CompletableFuture<CharacterProfileResponse> getCharacterItems(String realm, String characterName) {
		return toFuture(retrofitImpl.getCharacterByFields(realm, characterName, CharacterFieldEnum.ITEMS.getContext()));
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
            } else {
//                completeExceptionally(EXCEPTION_CONVERTER.apply(response));
            }
        }

        @Override
        public void onFailure(final Call<T> call, final Throwable t) {
            completeExceptionally(
                    new Exception(t.getMessage()));
        }
    }
}
