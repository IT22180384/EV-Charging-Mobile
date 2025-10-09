package com.example.evcharging.application;

import android.app.Application;

import com.example.evcharging.utils.SpUtil;

public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SpUtil.init(this);
    }
}