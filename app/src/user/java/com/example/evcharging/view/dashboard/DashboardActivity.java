package com.example.evcharging.view.dashboard;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.evcharging.R;
import com.example.evcharging.databinding.ActivityDashboardBinding;
import com.example.evcharging.view.base.BaseActivity;

public class DashboardActivity extends BaseActivity {
    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDashboardContent();
        showNavMenu(); // Show navigation for this activity
    }

    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    private void loadDashboardContent() {
        // Load dashboard content into the base activity's content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            binding = ActivityDashboardBinding.inflate(getLayoutInflater());
            contentContainer.addView(binding.getRoot());
        }
    }
}