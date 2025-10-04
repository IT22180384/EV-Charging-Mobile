package com.example.evcharging.view.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.example.evcharging.R;
import com.example.evcharging.view.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadContent();
        showNavMenu(); // Show navigation for this activity
    }

    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void loadContent() {
        // Load the home fragment content into the content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            View homeView = LayoutInflater.from(this).inflate(R.layout.fragment_home, contentContainer, false);
            contentContainer.addView(homeView);
        }
    }
}