package com.example.evcharging.view.settings;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.evcharging.R;
import com.example.evcharging.databinding.ActivitySettingsBinding;
import com.example.evcharging.view.base.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettingsContent();
        showNavMenu(); // Show navigation for this activity
    }

    private void loadSettingsContent() {
        // Load settings content into the base activity's content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            binding = ActivitySettingsBinding.inflate(getLayoutInflater());
            contentContainer.addView(binding.getRoot());
        }
    }

    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_settings);
        }
    }
}