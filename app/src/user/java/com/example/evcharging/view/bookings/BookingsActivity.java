package com.example.evcharging.view.bookings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.evcharging.R;
import com.example.evcharging.view.base.BaseActivity;

public class BookingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the bookings content into the content container
        loadBookingsContent();
    }
    
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_base;
    }
    
    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_bookings);
        }
    }
    
    private void loadBookingsContent() {
        // Inflate the bookings layout into the content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        LayoutInflater.from(this).inflate(R.layout.activity_bookings, contentContainer, true);
    }
}
