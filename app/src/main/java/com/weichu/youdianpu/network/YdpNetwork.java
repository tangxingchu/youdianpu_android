package com.weichu.youdianpu.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YdpNetwork {

    public static final String IGNORE_INTERCEPTORS_HEADER = "youdianpuignoreinterceptors";

    private static final String CACHE_DIR = "okhttp";
    private static final int ONE_YEAR_IN_SECONDS = 60 * 60 * 24 * 365;
    private static final int ONE_DAY_IN_SECONDS = 60 * 60 * 24;

    private Context mContext;
    private YdpHttpClient mClient;
    private YdpHttpClient mLongTimeoutClient;
    private OkHttpClient mNoCacheClient;

    private YdpNetwork mYdpNetwork;

    public static void flushResponse(Response response) throws IOException {
        response.body().bytes();
    }

    public interface OkHttpClientFactory {
        OkHttpClient getNewClient();
    }

    public YdpNetwork getInstance(Context context) {
        if(mYdpNetwork == null) {
            mYdpNetwork = new YdpNetwork(context);
        }
        return mYdpNetwork;
    }

    private YdpNetwork(Context context) {
        mContext = context.getApplicationContext();

        mClient = new YdpHttpClient(mContext, new OkHttpClientFactory() {
            @Override
            public OkHttpClient getNewClient() {
                return createHttpClientBuilder().build();
            }
        });

        mLongTimeoutClient = new YdpHttpClient(mContext, new OkHttpClientFactory() {
            @Override
            public OkHttpClient getNewClient() {
                OkHttpClient longTimeoutHttpClient = createHttpClientBuilder()
                        .readTimeout(2, TimeUnit.MINUTES)
                        .build();
                return longTimeoutHttpClient;
            }
        });

        mNoCacheClient = new OkHttpClient.Builder().build();
    }

    private OkHttpClient.Builder createHttpClientBuilder() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .cache(getCache());
        addInterceptors(clientBuilder);
        return clientBuilder;
    }

    public YdpHttpClient getClient() {
        return mClient;
    }

    public YdpHttpClient getLongTimeoutClient() {
        return mLongTimeoutClient;
    }

    // Warning: this doesn't WRITE to the cache either. Don't use this to populate the cache in the background.
    public OkHttpClient getNoCacheClient() {
        return mNoCacheClient;
    }

    public Cache getCache() {
        int cacheSize = 40 * 1024 * 1024; // 40 MiB
        // Use getFilesDir() because it gives us much more space than getCacheDir()
        final File directory = new File(mContext.getFilesDir(), CACHE_DIR);
        return new Cache(directory, cacheSize);
    }

    public boolean isNetworkAvailable() {
        return isNetworkAvailable(mContext);
    }

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void addInterceptors(OkHttpClient.Builder clientBuilder) {

        Interceptor offlineInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                boolean isNetworkAvailable = isNetworkAvailable();
                // Request
                Request originalRequest = chain.request();
                if (originalRequest.header(IGNORE_INTERCEPTORS_HEADER) != null) {
                    return noopInterceptor(chain, originalRequest);
                }

                Request request;
                if (isNetworkAvailable) {
                    request = originalRequest.newBuilder()
                            .removeHeader("Cache-Control")
                            .build();
                } else {
                    // If network isn't available we don't care if the cache is stale.
                    request = originalRequest.newBuilder()
                            .header("Cache-Control", "max-stale=" + ONE_YEAR_IN_SECONDS)
                            .build();
                }

                // Response
                Response response = chain.proceed(request);
                String responseCacheHeaderValue;
                if (isNetworkAvailable) {
                    String currentResponseHeader = response.header("Cache-Control");
                    if (currentResponseHeader != null && currentResponseHeader.contains("public") &&
                            (currentResponseHeader.contains("max-age") || currentResponseHeader.contains("s-maxage"))) {
                        // Server sent back caching instructions, follow them
                        responseCacheHeaderValue = currentResponseHeader;
                    } else {
                        // Server didn't send Cache-Control header or told us not to cache. Tell OkHttp
                        // to cache the response but invalidate it after 0 seconds. This will allow us
                        // to access the response with max-stale if the network is turned off.
                        responseCacheHeaderValue = "public, max-age=0";
                    }
                } else {
                    // Only read from the cache, don't try to hit the network
                    responseCacheHeaderValue = "public, only-if-cached";
                }

                return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", responseCacheHeaderValue)
                        .build();
            }
        };

        clientBuilder.addInterceptor(offlineInterceptor);
        clientBuilder.addNetworkInterceptor(offlineInterceptor);
    }

    public void addTokenInterceptor(String token) {
        TokenInterceptor tokenInterceptor = new TokenInterceptor(token);
        
    }

    private static Response noopInterceptor(Interceptor.Chain chain, Request originalRequest) throws IOException {
        Request request = originalRequest.newBuilder().removeHeader(IGNORE_INTERCEPTORS_HEADER).build();
        return chain.proceed(request);
    }
}
