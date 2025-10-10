package com.example.evcharging.application;

import android.app.Application;

import com.example.evcharging.utils.SpUtil;

public class OperatorApplication extends Application {
    private static OperatorApplication instance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SpUtil.init(this);
    }

    public static OperatorApplication getInstance() {
        return instance;
    }
}