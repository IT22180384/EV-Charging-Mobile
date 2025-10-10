package com.example.evcharging.http;

import com.example.evcharging.BuildConfig;
import com.example.evcharging.utils.SpUtil;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitProvider {

    private static volatile RetrofitProvider instance;
    private final Retrofit retrofit;

    private RetrofitProvider() {
        OkHttpClient client = buildClient();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RetrofitProvider getInstance() {
        if (instance == null) {
            synchronized (RetrofitProvider.class) {
                if (instance == null) {
                    instance = new RetrofitProvider();
                }
            }
        }
        return instance;
    }

    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    private OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // Attach Authorization header if token exists
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws java.io.IOException {
                Request original = chain.request();
                String token = SpUtil.getAccessToken();
                if (token != null && !token.isEmpty()) {
                    Request authed = original.newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(authed);
                }
                return chain.proceed(original);
            }
        });

        HttpUrl baseUrl = HttpUrl.parse(BuildConfig.BASE_URL);
        boolean isLocalDebugHost = baseUrl != null && isLocalHost(baseUrl.host());

        if (BuildConfig.DEBUG && isLocalDebugHost) {
            // Debug-only self-signed HTTPS support (use only for local dev)
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                            @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
                        }
                };
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier((hostname, session) ->
                        "10.0.2.2".equals(hostname) ||
                        "localhost".equals(hostname) ||
                        hostname.endsWith(".devtunnels.ms"));
            } catch (Exception ignored) {
            }
        }

        return builder.build();
    }

    private boolean isLocalHost(String host) {
        if (host == null) {
            return false;
        }
        return "10.0.2.2".equals(host) ||
                "localhost".equals(host) ||
                host.endsWith(".devtunnels.ms");
    }
}
