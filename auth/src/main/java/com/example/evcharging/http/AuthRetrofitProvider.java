package com.example.evcharging.http;

import com.example.evcharging.auth.BuildConfig;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthRetrofitProvider {

    private static volatile AuthRetrofitProvider instance;
    private final Retrofit retrofit;

    private AuthRetrofitProvider() {
        OkHttpClient client = buildClient();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static AuthRetrofitProvider getInstance() {
        if (instance == null) {
            synchronized (AuthRetrofitProvider.class) {
                if (instance == null) {
                    instance = new AuthRetrofitProvider();
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
