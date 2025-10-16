package com.example.evcharging.application;

import android.app.Application;

import com.example.evcharging.data.local.DBHelper;
import com.example.evcharging.data.sync.OfflineSyncManager;
import com.example.evcharging.utils.SpUtil;

public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SpUtil.init(this);
        DBHelper.getInstance(this);
        OfflineSyncManager.init(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
