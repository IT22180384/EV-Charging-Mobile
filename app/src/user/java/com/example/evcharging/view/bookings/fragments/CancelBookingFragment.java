package com.example.evcharging.view.bookings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evcharging.databinding.FragmentCancelBookingBinding;
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.BookingActionListener;
import com.example.evcharging.viewmodel.BookingViewModel;

public class CancelBookingFragment extends Fragment {
    private FragmentCancelBookingBinding binding;
    private BookingActionListener listener;
    private String bookingId;
    private com.example.evcharging.model.Booking booking;
    private BookingViewModel viewModel;

    public static CancelBookingFragment newInstance() {
        CancelBookingFragment fragment = new CancelBookingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static CancelBookingFragment newInstance(com.example.evcharging.model.Booking booking) {
        CancelBookingFragment fragment = new CancelBookingFragment();
        Bundle args = new Bundle();
        args.putSerializable("booking", booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCancelBookingBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);

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
        // Get booking from arguments if available
        if (getArguments() != null && getArguments().containsKey("booking")) {
            booking = (com.example.evcharging.model.Booking) getArguments().getSerializable("booking");
            if (booking != null) {
                displayBookingDetails(booking);
            }
        } else if (bookingId != null) {
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
                "RES-" + bookingId,
                "STATION-123",
                "Downtown Charging Station",
                "123 Liberty Ave, City Center",
                "Dec 15, 2024",
                "2:00 PM - 4:00 PM",
                "Slot A2",
                Booking.Status.PENDING,
                "Pending",
                true,
                true,
                "2024-12-15T14:00:00",
                "2024-12-15T15:00:00"
        );
    }

    private void displayBookingDetails(Booking booking) {
        if (binding == null) return;
        
        // Update the booking details in the UI
        binding.textStationName.setText(booking.getStationName());
        binding.textBookingDate.setText(booking.getDate());
        binding.textBookingTime.setText(booking.getTimeRange());
        binding.textSlot.setText(booking.getSlot());
    }

    private void confirmCancellation() {
        if (booking == null || booking.getReservationId() == null) {
            Toast.makeText(getContext(), "Booking information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Debug log to check the reservation ID
        android.util.Log.d("CancelBooking", "Attempting to cancel reservation ID: " + booking.getReservationId());
        android.util.Log.d("CancelBooking", "Booking ID: " + booking.getBookingId());
        android.util.Log.d("CancelBooking", "Station ID: " + booking.getStationId());

        // Disable the cancel button to prevent multiple clicks
        binding.btnCancelBooking.setEnabled(false);
        binding.btnCancelBooking.setText("Cancelling...");

        // Call the API to cancel the reservation
        viewModel.cancelReservation(booking.getReservationId(), new BookingViewModel.ReservationActionCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                
                Toast.makeText(getContext(), "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                
                if (listener != null) {
                    listener.onBookingCancelled(booking.getBookingId());
                }
                
                // Navigate back
                showBottomNavigation();
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                
                // Re-enable the button
                binding.btnCancelBooking.setEnabled(true);
                binding.btnCancelBooking.setText("Cancel Booking");
                
                Toast.makeText(getContext(), 
                    errorMessage != null ? errorMessage : "Failed to cancel booking", 
                    Toast.LENGTH_SHORT).show();
            }
        });
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
