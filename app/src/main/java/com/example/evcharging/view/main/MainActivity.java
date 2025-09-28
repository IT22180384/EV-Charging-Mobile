package com.example.evcharging.view.main;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.evcharging.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    
    private BottomNavigationView bottomNavigation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_nav);
        
        // Initialize bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        
        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_home) {
            // Load Home Fragment
            loadFragment("Home");
            return true;
        } else if (itemId == R.id.nav_dashboard) {
            // Load Dashboard Fragment
            loadFragment("Dashboard");
            return true;
        } else if (itemId == R.id.nav_bookings) {
            // Load Bookings Fragment
            loadFragment("Bookings");
            return true;
        } else if (itemId == R.id.nav_settings) {
            // Load Settings Fragment
            loadFragment("Settings");
            return true;
        } else if (itemId == R.id.nav_profile) {
            // Load Profile Fragment
            loadFragment("Profile");
            return true;
        }
        return false;
    }
    
    private void loadFragment(String fragmentName) {
        // This is a placeholder - you would load actual fragments here
        // For now, we'll just show a simple text view
        // In a real implementation, you would replace this with actual fragment loading
    }
}
