package com.cc.wcl.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.cc.wcl.rank.CharacterRankResponse;

import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AllArgsConstructor
public class WarcraftLogsClientImpl implements WarcraftLogsClient {
	
    private final WarcraftLogsService retrofitImpl;
	
	@Override
	public CompletableFuture<List<CharacterRankResponse>> getRankingsByCharacter(String characterName, String serverName, String serverRegion, String metric) {
		return toFuture(retrofitImpl.getRankingsByCharacter(characterName, serverName, serverRegion, metric));
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
