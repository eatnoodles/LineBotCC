package com.cc.google.client;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.IOUtils;

import com.cc.google.client.exception.GoogleApiException;
import com.cc.google.shortener.ShortenParam;
import com.cc.google.shortener.ShortenUrl;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AllArgsConstructor
public class GoogleClientImpl implements GoogleClient {
	
    private final GoogleApiService retrofitImpl;
	
	@Override
	public CompletableFuture<ShortenUrl> getShortenURL(String url) {
		return toFuture(retrofitImpl.getShortenURL(new ShortenParam(url)));
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
    			try {
    				if (response.code() == 404) {
    					StringWriter writer = new StringWriter();
    					IOUtils.copy(response.errorBody().byteStream(), writer, "UTF-8");
    					String jsonStr = writer.toString();
    					ObjectMapper mapper = new ObjectMapper();
    					String errorMsg = mapper.readTree(jsonStr).get("reason").textValue();
    					
    					completeExceptionally(new GoogleApiException(errorMsg));
    				} else {
    					completeExceptionally(new Exception("error"));
    				}
				} catch (IOException e) {
					completeExceptionally(new Exception("error"));
				}
            }
        }

        @Override
        public void onFailure(final Call<T> call, final Throwable t) {
            completeExceptionally(
                    new Exception(t.getMessage()));
        }
    }
}
