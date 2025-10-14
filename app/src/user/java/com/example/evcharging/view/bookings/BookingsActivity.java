package com.example.evcharging.view.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.evcharging.R;
import com.example.evcharging.databinding.ActivityBookingsBinding;
import com.example.evcharging.view.base.BaseActivity;
import com.example.evcharging.view.bookings.adapters.BookingsPagerAdapter;
import com.example.evcharging.view.bookings.fragments.BookingDetailsFragment;
import com.example.evcharging.view.bookings.fragments.BookingStepOneFragment;
import com.example.evcharging.view.bookings.fragments.BookingStepTwoFragment;
import com.example.evcharging.view.bookings.fragments.BookingStepThreeFragment;
import com.example.evcharging.view.bookings.fragments.CancelBookingFragment;
import com.example.evcharging.view.bookings.fragments.HistoryBookingsFragment;
import com.example.evcharging.view.bookings.fragments.ModifyBookingFragment;
import com.example.evcharging.view.bookings.fragments.UpcomingBookingsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BookingsActivity extends BaseActivity implements BookingActionListener, View.OnClickListener {
    private ActivityBookingsBinding binding;
    private boolean isShowingNewBooking = false;
    private int currentStep = 1;
    private OnBackPressedCallback backPressedCallback;

    private String currentStationId = "";
    private String currentStationName = "";
    private String currentDate = "";
    private String currentTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadBookingsContent();
        showNavMenu(); // Show navigation for this activity
        initViews();
        setupBackPressedCallback();

        // Check for direct navigation to step two
        Intent intent = getIntent();
        if (intent.hasExtra("station_id")) {
            String stationId = intent.getStringExtra("station_id");
            String stationName = intent.getStringExtra("station_name");
            startBookingAtStepTwo(stationId, stationName);
        }
    }
    
    private void setupBackPressedCallback() {
        backPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                handleBackNavigation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    private void handleBackNavigation() {
        if (isShowingNewBooking) {
            if (currentStep > 1) {
                navigateBackToPreviousStep();
            } else {
                exitBookingFlow();
            }
        }
    }

    private void navigateBackToPreviousStep() {
        switch (currentStep) {
            case 2:
                goBackToStepOne();
                break;
            case 3:
                goBackToStepTwo(currentStationId, currentStationName);
                break;
        }
    }

    private void exitBookingFlow() {
        showNavMenu();

        if (binding != null) {
            binding.getRoot().setVisibility(View.VISIBLE);
        }

        FrameLayout contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            contentContainer.removeAllViews();
            contentContainer.addView(binding.getRoot());
        }

        isShowingNewBooking = false;
        currentStep = 1;
        backPressedCallback.setEnabled(false);

        clearBookingData();
    }

    private void clearBookingData() {
        currentStationId = "";
        currentStationName = "";
        currentDate = "";
        currentTime = "";
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

        // Start with step 1 of the booking process
        currentStep = 1;
        showBookingFragment(new BookingStepOneFragment());

        isShowingNewBooking = true;
        backPressedCallback.setEnabled(true);
    }

    private void showBookingFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goBackToStepOne() {
        currentStep = 1;
        clearBookingData();
        showBookingFragment(new BookingStepOneFragment());
    }

    @Override
    public void navigateToStepTwo(String stationId, String stationName) {
        currentStep = 2;
        currentStationId = stationId;
        currentStationName = stationName;
        BookingStepTwoFragment fragment = BookingStepTwoFragment.newInstance(stationId, stationName);
        showBookingFragment(fragment);
    }

    @Override
    public void navigateToStepThree(String stationId, String stationName, String date, String time) {
        currentStep = 3;
        currentStationId = stationId;
        currentStationName = stationName;
        currentDate = date;
        currentTime = time;
        BookingStepThreeFragment fragment = BookingStepThreeFragment.newInstance(stationId, stationName, date, time);
        showBookingFragment(fragment);
    }

    public void goBackToStepTwo(String stationId, String stationName) {
        currentStep = 2;
        currentStationId = stationId;
        currentStationName = stationName;
        // Clear step 3 data
        currentDate = "";
        currentTime = "";
        BookingStepTwoFragment fragment = BookingStepTwoFragment.newInstance(stationId, stationName);
        showBookingFragment(fragment);
    }

    public void completeBooking() {
        // Handle booking completion
        exitBookingFlow();
        
        // Refresh the upcoming bookings tab after creating a new booking
        if (binding != null && binding.bookingsViewPager != null) {
            binding.getRoot().postDelayed(() -> {
                // Switch to upcoming bookings tab (index 0) and refresh
                binding.bookingsViewPager.setCurrentItem(0);
                refreshCurrentFragment();
            }, 200);
        }
    }

    public void cancelBooking() {
        // Handle booking cancellation
        exitBookingFlow();
    }

    @Override
    public void onBookingCancelled(String bookingId) {
        // Navigate back to the bookings list
        exitBookingFlow();
        
        // Refresh the current fragment after a short delay to ensure it's visible
        if (binding != null && binding.bookingsViewPager != null) {
            binding.getRoot().postDelayed(() -> {
                refreshCurrentFragment();
            }, 200);
        }
    }

    private void refreshCurrentFragment() {
        if (binding != null && binding.bookingsViewPager != null) {
            int currentItem = binding.bookingsViewPager.getCurrentItem();
            
            // Get the fragment adapter and force refresh
            BookingsPagerAdapter adapter = (BookingsPagerAdapter) binding.bookingsViewPager.getAdapter();
            if (adapter != null) {
                // Try multiple approaches to get the fragment and refresh it
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + currentItem);
                
                if (fragment instanceof UpcomingBookingsFragment) {
                    ((UpcomingBookingsFragment) fragment).refreshBookings();
                } else if (fragment instanceof HistoryBookingsFragment) {
                    ((HistoryBookingsFragment) fragment).refreshBookings();
                } else {
                    // Fallback: recreate the adapter to force refresh
                    binding.bookingsViewPager.setAdapter(new BookingsPagerAdapter(this));
                    setupTabLayoutMediator();
                }
            }
        }
    }

    private void setupTabLayoutMediator() {
        if (binding != null && binding.bookingsViewPager != null && binding.bookingsTabLayout != null) {
            new TabLayoutMediator(binding.bookingsTabLayout, binding.bookingsViewPager, (tab, position) -> {
                if (position == 0) {
                    tab.setText(R.string.label_bookings_upcoming);
                } else {
                    tab.setText(R.string.label_bookings_history);
                }
            }).attach();
        }
    }

    @Override
    public void onBookingModified(String bookingId) {

    }

    @Override
    public void navigateToModifyBooking() {
        showBookingFragment(ModifyBookingFragment.newInstance());
    }

    @Override
    public void navigateToCancelBooking() {
        showBookingFragment(CancelBookingFragment.newInstance());
    }

    @Override
    public void navigateToCancelBooking(com.example.evcharging.model.Booking booking) {
        showBookingFragment(CancelBookingFragment.newInstance(booking));
    }

    @Override
    public void navigateToBookingDetails() {
        showBookingFragment(BookingDetailsFragment.newInstance());
    }

    @Override
    public void navigateToBookingDetails(com.example.evcharging.model.Booking booking) {
        showBookingFragment(BookingDetailsFragment.newInstance(booking));
    }

    @Override
    protected void onDestroy() {
        if (backPressedCallback != null) {
            backPressedCallback.remove();
        }
        super.onDestroy();
    }

    private void startBookingAtStepTwo(String stationId, String stationName) {
        // Hide the navigation menu
        hideNavMenu();

        // Hide the current bookings content
        if (binding != null) {
            binding.getRoot().setVisibility(View.GONE);
        }

        // Start directly at step 2 of the booking process
        currentStep = 2;
        navigateToStepTwo(stationId, stationName);

        isShowingNewBooking = true;
        backPressedCallback.setEnabled(true);
    }
}
