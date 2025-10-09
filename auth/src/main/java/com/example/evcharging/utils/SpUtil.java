package com.example.evcharging.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
    private static final String PREFS_NAME = "ev_charging_prefs";
    private static Context appContext;

    // Shared preference keys
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_NIC = "nic";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_LOGIN_TIMESTAMP = "login_timestamp";

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    private static Context getContext() {
        if (appContext == null) {
            throw new IllegalStateException("SpUtil must be initialized with init(context) before use");
        }
        return appContext;
    }

    private static SharedPreferences getSharedPreferences() {
        return getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key, long defaultValue) {
        return getSharedPreferences().getLong(key, defaultValue);
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clear() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.apply();
    }

    // Authentication specific methods
    public static void saveLoginState(boolean isLoggedIn) {
        putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        if (isLoggedIn) {
            putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis());
        }
    }

    public static boolean isLoggedIn() {
        return getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static void saveUserCredentials(String accessToken, String refreshToken,
                                           String userId, String email, String name, String nic, String userType) {
        putString(KEY_ACCESS_TOKEN, accessToken);
        if (refreshToken != null) putString(KEY_REFRESH_TOKEN, refreshToken);
        if (userId != null) putString(KEY_USER_ID, userId);
        if (email != null) putString(KEY_USER_EMAIL, email);
        if (name != null) putString(KEY_USER_NAME, name);
        if (nic != null) putString(KEY_NIC, nic);
        if (userType != null) putString(KEY_USER_TYPE, userType);
        saveLoginState(true);
    }

    public static void logout() {
        // Clear all user-related data
        remove(KEY_IS_LOGGED_IN);
        remove(KEY_ACCESS_TOKEN);
        remove(KEY_REFRESH_TOKEN);
        remove(KEY_USER_ID);
        remove(KEY_USER_EMAIL);
        remove(KEY_USER_NAME);
        remove(KEY_NIC);
        remove(KEY_USER_TYPE);
        remove(KEY_LOGIN_TIMESTAMP);
    }

    public static String getAccessToken() {
        return getString(KEY_ACCESS_TOKEN);
    }

    public static String getUserId() {
        return getString(KEY_USER_ID);
    }

    public static String getUserEmail() {
        return getString(KEY_USER_EMAIL);
    }

    public static String getUserName() {
        return getString(KEY_USER_NAME);
    }

    public static String getNic() {
        return getString(KEY_NIC);
    }

    public static String getUserType() {
        return getString(KEY_USER_TYPE);
    }
}