package com.example.evcharging.view.bookings;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.evcharging.R;
import com.example.evcharging.databinding.ActivityBookingsBinding;
import com.example.evcharging.view.base.BaseActivity;
import com.example.evcharging.view.bookings.adapters.BookingsPagerAdapter;
import com.example.evcharging.view.bookings.fragments.NewReservationFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BookingsActivity extends BaseActivity implements View.OnClickListener {
    private ActivityBookingsBinding binding;
    private boolean isShowingNewBooking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadBookingsContent();
        showNavMenu(); // Show navigation for this activity
        initViews();
    }

    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_bookings);
        }
    }

    private void initViews() {
        if (binding != null) {
            binding.buttonNewBooking.setOnClickListener(this);
            setupViewPager();
        }
    }

    private void loadBookingsContent() {
        // Load bookings content into the base activity's content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            binding = ActivityBookingsBinding.inflate(getLayoutInflater());
            contentContainer.addView(binding.getRoot());
        }
    }

    private void setupViewPager() {
        ViewPager2 viewPager = binding.bookingsViewPager;
        TabLayout tabLayout = binding.bookingsTabLayout;

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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_new_booking) {
            showNewBookingFragment();
        }
    }

    private void showNewBookingFragment() {
        // Hide the navigation menu
        hideNavMenu();

        // Hide the current bookings content
        if (binding != null) {
            binding.getRoot().setVisibility(View.GONE);
        }

        // Get the main content container from the base activity
        FrameLayout contentContainer = findViewById(R.id.content_container);

        // Create and show the NewReservationFragment
        NewReservationFragment newBookingFragment = new NewReservationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_container, newBookingFragment, "new_booking_fragment");
        transaction.addToBackStack(null);
        transaction.commit();

        isShowingNewBooking = true;
    }

    @Override
    public void onBackPressed() {
        if (isShowingNewBooking) {
            // Show the navigation menu back
            showNavMenu();

            // Show the bookings content back
            if (binding != null) {
                binding.getRoot().setVisibility(View.VISIBLE);
            }

            // Remove the fragment
            getSupportFragmentManager().popBackStack();

            isShowingNewBooking = false;
        } else {
            super.onBackPressed();
        }
    }
}