package com.example.evcharging.view.bookings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.evcharging.databinding.FragmentCancelBookingBinding;
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.BookingActionListener;

public class CancelBookingFragment extends Fragment {
    private FragmentCancelBookingBinding binding;
    private BookingActionListener listener;
    private String bookingId;

    public static CancelBookingFragment newInstance() {
        CancelBookingFragment fragment = new CancelBookingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCancelBookingBinding.inflate(inflater, container, false);

        // Hide bottom navigation when this fragment is displayed
        hideBottomNavigation();

        initializeViews();
        setupListeners();

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BookingActionListener) {
            listener = (BookingActionListener) context;
        } else {
            throw new RuntimeException("Hosting activity must implement BookingActionListener");
        }
    }

    private void initializeViews() {

        // Load booking details if bookingId is provided
        if (bookingId != null) {
            loadBookingDetails(bookingId);
        }
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> {
            showBottomNavigation();
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.btnCancelBooking.setOnClickListener(v -> confirmCancellation());

        binding.btnKeepBooking.setOnClickListener(v -> {
            showBottomNavigation();
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadBookingDetails(String bookingId) {
        // Simulate loading booking details - in a real app this would come from a repository
        Booking booking = findBookingById(bookingId);
        if (booking != null) {
            displayBookingDetails(booking);
        }
    }

    private Booking findBookingById(String bookingId) {
        // Simulate finding booking by ID - in a real app this would query the repository
        return new Booking(
                bookingId,
                "Downtown Charging Station",
                "123 Liberty Ave, City Center",
                "Dec 15, 2024",
                "2:00 PM - 4:00 PM",
                "Slot A2",
                Booking.Status.PENDING,
                "Pending"
        );
    }

    private void displayBookingDetails(Booking booking) {
        // The layout uses hardcoded text for booking details in this design
        // In a real implementation, you would find TextViews with IDs and update them
        // For now, the booking details are displayed in the static layout
    }

    private void confirmCancellation() {
        // Handle booking cancellation logic
        if (listener != null) {
            listener.onBookingCancelled(bookingId);
        }
        // Show bottom navigation after cancellation action
        showBottomNavigation();
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show bottom navigation when leaving this fragment
        showBottomNavigation();
        binding = null;
    }

    private void hideBottomNavigation() {
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(com.example.evcharging.R.id.bottom_navigation_container);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }
    }

    private void showBottomNavigation() {
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(com.example.evcharging.R.id.bottom_navigation_container);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }
    }
}