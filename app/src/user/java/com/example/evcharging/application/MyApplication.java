package com.example.evcharging.application;

import android.app.Application;
import android.content.Context;
import com.example.evcharging.data.TokenManager;

public class MyApplication extends Application {
    private static MyApplication instance;
    private static String token;
    private static String userId;
    private static String nic;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Warm cache from preferences
        token = TokenManager.getToken(this);
        userId = TokenManager.getUserId(this);
        nic = TokenManager.getNic(this);
    }

    public static Context getAppContext() {
        return instance;
    }

    public static void setAuth(String t, String uId, String n) {
        token = t; userId = uId; nic = n;
        if (instance != null) {
            TokenManager.saveToken(instance, t);
            if (uId != null) TokenManager.saveUserId(instance, uId);
            if (n != null) TokenManager.saveNic(instance, n);
        }
    }

    public static String getToken() {
        if (token == null && instance != null) token = TokenManager.getToken(instance);
        return token;
    }

    public static String getUserId() {
        if (userId == null && instance != null) userId = TokenManager.getUserId(instance);
        return userId;
    }

    public static String getNic() {
        if (nic == null && instance != null) nic = TokenManager.getNic(instance);
        return nic;
    }
}
