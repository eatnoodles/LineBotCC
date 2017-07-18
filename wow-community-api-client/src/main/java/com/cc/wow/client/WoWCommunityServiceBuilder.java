package com.cc.wow.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.NonNull;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * 
 * @author Caleb Cheng
 *
 */
public final class WoWCommunityServiceBuilder {
	public static final String DEFAULT_API_END_POINT = "https://tw.api.battle.net/wow/";
	public static final String DEFAULT_LOCALE = "zh_TW";
    public static final long DEFAULT_CONNECT_TIMEOUT = 10_000;
    public static final long DEFAULT_READ_TIMEOUT = 10_000;
    public static final long DEFAULT_WRITE_TIMEOUT = 10_000;

    private String apiEndPoint = DEFAULT_API_END_POINT;
    private long connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private long readTimeout = DEFAULT_READ_TIMEOUT;
    private long writeTimeout = DEFAULT_WRITE_TIMEOUT;
    private List<Interceptor> interceptors = new ArrayList<>();

    private OkHttpClient.Builder okHttpClientBuilder;
    private Retrofit.Builder retrofitBuilder;

    /**
     * Create a new {@link WoWCommunityServiceBuilder} with specified apikey.
     */
    public static WoWCommunityServiceBuilder create(@NonNull String apikey) {
        return new WoWCommunityServiceBuilder(defaultInterceptors(apikey));
    }

    private WoWCommunityServiceBuilder(List<Interceptor> interceptors) {
        this.interceptors.addAll(interceptors);
    }

    /**
     * Set apiEndPoint.
     */
    public WoWCommunityServiceBuilder apiEndPoint(@NonNull String apiEndPoint) {
        this.apiEndPoint = apiEndPoint;
        return this;
    }

    /**
     * Set connectTimeout in milliseconds.
     */
    public WoWCommunityServiceBuilder connectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * Set readTimeout in milliseconds.
     */
    public WoWCommunityServiceBuilder readTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Set writeTimeout in milliseconds.
     */
    public WoWCommunityServiceBuilder writeTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    /**
     * Add interceptor
     */
    public WoWCommunityServiceBuilder addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
        return this;
    }

    /**
     * Add interceptor first
     */
    public WoWCommunityServiceBuilder addInterceptorFirst(Interceptor interceptor) {
        this.interceptors.add(0, interceptor);
        return this;
    }

    /**
     * <p>If you want to use your own setting, specify {@link OkHttpClient.Builder} instance.</p>
     */
    public WoWCommunityServiceBuilder okHttpClientBuilder(@NonNull OkHttpClient.Builder okHttpClientBuilder) {
        this.okHttpClientBuilder = okHttpClientBuilder;
        return this;
    }

    /**
     * <p>If you want to use your own setting, specify {@link Retrofit.Builder} instance.</p>
     *
     * <p>ref: {@link WoWCommunityServiceBuilder#createDefaultRetrofitBuilder()} ()}.</p>
     */
    public WoWCommunityServiceBuilder retrofitBuilder(@NonNull Retrofit.Builder retrofitBuilder) {
        this.retrofitBuilder = retrofitBuilder;
        return this;
    }

    /**
     * Creates a new {@link WoWCommunityService}.
     */
    public WoWCommunityService build() {
        if (okHttpClientBuilder == null) {
            okHttpClientBuilder = new OkHttpClient.Builder();
            interceptors.forEach(okHttpClientBuilder::addInterceptor);
        }
        okHttpClientBuilder
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        final OkHttpClient okHttpClient = okHttpClientBuilder.build();

        if (retrofitBuilder == null) {
            retrofitBuilder = createDefaultRetrofitBuilder();
        }
        retrofitBuilder.client(okHttpClient);
        retrofitBuilder.baseUrl(apiEndPoint);
        final Retrofit retrofit = retrofitBuilder.build();

        return retrofit.create(WoWCommunityService.class);
    }

    private static List<Interceptor> defaultInterceptors(String apiKey) {
        final Logger slf4jLogger = LoggerFactory.getLogger("com.cc.wow.client.wire");
        final HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(message -> slf4jLogger.info("{}", message));
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return Arrays.asList(
                new RequestInterceptor(apiKey, DEFAULT_LOCALE),
                httpLoggingInterceptor
        );
    }

    private static Retrofit.Builder createDefaultRetrofitBuilder() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Register JSR-310(java.time.temporal.*) module and read number as millsec.
        objectMapper.registerModule(new JavaTimeModule())
                    .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        return new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(objectMapper));
    }
}
