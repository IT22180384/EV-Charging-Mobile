package com.example.evcharging.view.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.evcharging.R;
import com.example.evcharging.view.base.BaseActivity;

public class DashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the dashboard content into the content container
        loadDashboardContent();
    }
    
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_base;
    }
    
    @Override
    protected String getActivityTitle() {
        return "Dashboard";
    }
    
    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        }
    }
    
    private void loadDashboardContent() {
        // Inflate the dashboard layout into the content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        LayoutInflater.from(this).inflate(R.layout.activity_dashboard, contentContainer, true);
    }
}