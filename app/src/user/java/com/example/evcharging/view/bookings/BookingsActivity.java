package com.example.evcharging.view.bookings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.example.evcharging.R;
import com.example.evcharging.view.base.BaseActivity;
import com.example.evcharging.view.bookings.adapters.BookingsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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

        TabLayout tabLayout = findViewById(R.id.bookings_tab_layout);
        ViewPager2 viewPager = findViewById(R.id.bookings_view_pager);

        viewPager.setAdapter(new BookingsPagerAdapter(this));
        viewPager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.label_bookings_upcoming);
            } else {
                tab.setText(R.string.label_bookings_history);
            }
        }).attach();
    }
}
