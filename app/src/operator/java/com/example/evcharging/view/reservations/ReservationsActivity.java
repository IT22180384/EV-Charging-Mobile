package com.example.evcharging.view.reservations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.evcharging.R;
import com.example.evcharging.view.base.OperatorBaseActivity;

public class ReservationsActivity extends OperatorBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the reservations content into the content container
        loadReservationsContent();
    }
    
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_base;
    }
    
    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_reservations);
        }
    }
    
    private void loadReservationsContent() {
        // Inflate the reservations layout into the content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        LayoutInflater.from(this).inflate(R.layout.activity_reservations, contentContainer, true);
    }
}
