package com.example.evcharging.view.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evcharging.R;
import com.example.evcharging.view.bookings.BookingsActivity;
import com.example.evcharging.view.main.MainActivity;
import com.example.evcharging.view.profile.ProfileActivity;
import com.example.evcharging.view.settings.SettingsActivity;
import com.example.evcharging.view.stations.StationsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView bottomNavigation;
    private View bottomNavigationContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Setup bottom navigation
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the correct navigation item is selected when activity resumes
        if (bottomNavigation != null) {
            setSelectedNavigationItem();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigationContainer = findViewById(R.id.bottom_navigation_container);
        if (bottomNavigation != null) {
            bottomNavigation.setOnNavigationItemSelectedListener(this);
            // Set the correct selected item based on current activity
            // Use post to ensure the view is fully initialized
            bottomNavigation.post(new Runnable() {
                @Override
                public void run() {
                    setSelectedNavigationItem();
                }
            });
        }
    }

    /**
     * Show the bottom navigation menu
     */
    protected void showNavMenu() {
        if (bottomNavigationContainer != null) {
            bottomNavigationContainer.setVisibility(View.VISIBLE);
        }
        // Adjust content container margin when nav is visible
        View contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            int navHeight = getResources().getDimensionPixelSize(R.dimen.nav_height);
            contentContainer.setPadding(0, 0, 0, navHeight);
        }
    }

    /**
     * Hide the bottom navigation menu for full screen content
     */
    protected void hideNavMenu() {
        if (bottomNavigationContainer != null) {
            bottomNavigationContainer.setVisibility(View.GONE);
        }
        // Remove content container margin when nav is hidden
        View contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            contentContainer.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * Check if navigation menu is currently visible
     */
    protected boolean isNavMenuVisible() {
        return bottomNavigationContainer != null && bottomNavigationContainer.getVisibility() == View.VISIBLE;
    }

    protected abstract void setSelectedNavigationItem();

    protected void forceNavigationSelection(int itemId) {
        if (bottomNavigation != null) {
            // Simply set the selected item - let the system handle the highlighting
            bottomNavigation.setSelectedItemId(itemId);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Class<?> targetActivity = getTargetActivity(itemId);

        if (targetActivity != null && !this.getClass().equals(targetActivity)) {
            startActivity(new Intent(this, targetActivity));
            overridePendingTransition(0, 0);
            finish(); // Close current activity
            overridePendingTransition(0, 0);
            return true;
        }
        // If we're already on the target activity, just return true to show selection
        return true;
    }

    private Class<?> getTargetActivity(int itemId) {
        if (itemId == R.id.nav_home) {
            return MainActivity.class;
        } else if (itemId == R.id.nav_stations) {
            return StationsActivity.class;
        } else if (itemId == R.id.nav_bookings) {
            return BookingsActivity.class;
        } else if (itemId == R.id.nav_settings) {
            return SettingsActivity.class;
        } else if (itemId == R.id.nav_profile) {
            return ProfileActivity.class;
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}