package com.example.evcharging.view.profile;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.evcharging.R;
import com.example.evcharging.databinding.ActivityProfileBinding;
import com.example.evcharging.view.base.BaseActivity;

public class ProfileActivity extends BaseActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadProfileContent();
        showNavMenu(); // Show navigation for this activity
    }

    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
        }
    }

    private void loadProfileContent() {
        // Load profile content into the base activity's content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            binding = ActivityProfileBinding.inflate(getLayoutInflater());
            contentContainer.addView(binding.getRoot());
        }
    }
}