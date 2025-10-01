package com.example.evcharging.view.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.evcharging.R;
import com.example.evcharging.view.main.OperatorMainActivity;
import com.example.evcharging.view.qr.QRActivity;
import com.example.evcharging.view.profile.ProfileActivity;
import com.example.evcharging.view.settings.SettingsActivity;
import com.example.evcharging.view.reservations.ReservationsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class OperatorBaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    
    protected BottomNavigationView bottomNavigation;
    protected Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        
        // Setup toolbar
        setupToolbar();
        
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
    
    protected abstract int getLayoutResource();
    
    protected abstract String getActivityTitle();
    
    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getActivityTitle());
                // Only show back button if not OperatorMainActivity
                if (!(this instanceof OperatorMainActivity)) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }
    
    private void setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
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
    
    protected abstract void setSelectedNavigationItem();
    
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
            return OperatorMainActivity.class;
        } else if (itemId == R.id.nav_qr) {
            return QRActivity.class;
        } else if (itemId == R.id.nav_profile) {
            return ProfileActivity.class;
        } else if (itemId == R.id.nav_settings) {
            return SettingsActivity.class;
        } else if (itemId == R.id.nav_reservations) {
            return ReservationsActivity.class;
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
