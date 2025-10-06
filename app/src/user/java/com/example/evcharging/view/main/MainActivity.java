package com.example.evcharging.view.main;

import android.os.Bundle;

import com.example.evcharging.R;
import com.example.evcharging.view.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadContent(savedInstanceState);
        showNavMenu(); // Show navigation for this activity
    }

    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void loadContent(Bundle savedInstanceState) {
        // Load the home fragment layout directly
        if (savedInstanceState == null) {
            android.widget.FrameLayout contentContainer = findViewById(R.id.content_container);
            if (contentContainer != null) {
                android.view.View homeView = android.view.LayoutInflater.from(this).inflate(R.layout.fragment_home, contentContainer, false);
                contentContainer.addView(homeView);
            }
        }
    }
}