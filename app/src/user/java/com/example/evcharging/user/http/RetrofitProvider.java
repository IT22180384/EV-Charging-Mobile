package com.example.evcharging.user.http;

import com.example.evcharging.BuildConfig;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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

        if (BuildConfig.DEBUG) {
            // Debug-only self-signed HTTPS support (use only for local dev)
            try {
                javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                        new javax.net.ssl.X509TrustManager() {
                            @Override public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                            @Override public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                            @Override public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                        }
                };
                javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                builder.sslSocketFactory(sslSocketFactory, (javax.net.ssl.X509TrustManager) trustAllCerts[0]);
                builder.hostnameVerifier((hostname, session) ->
                        "10.0.2.2".equals(hostname) || "localhost".equals(hostname));
            } catch (Exception ignored) {
            }
        }

        return builder.build();
    }
}
