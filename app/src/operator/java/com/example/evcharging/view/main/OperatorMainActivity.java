package com.example.evcharging.view.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.evcharging.R;
import com.example.evcharging.view.base.OperatorBaseActivity;

public class OperatorMainActivity extends OperatorBaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the home content into the content container
        loadHomeContent();
    }
    
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_base;
    }
    
    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }
    
    private void loadHomeContent() {
        // Inflate the home layout into the content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        LayoutInflater.from(this).inflate(R.layout.fragment_operator_home, contentContainer, true);
    }
}
