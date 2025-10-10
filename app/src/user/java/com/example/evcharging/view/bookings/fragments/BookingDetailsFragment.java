package com.example.evcharging.view.bookings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.evcharging.databinding.FragmentBookingDetailsBinding;
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.BookingActionListener;

public class BookingDetailsFragment extends Fragment {
    private FragmentBookingDetailsBinding binding;
    private BookingActionListener listener;
    private Booking booking;
    private String bookingId;

    public static BookingDetailsFragment newInstance() {
        BookingDetailsFragment fragment = new BookingDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews();
        setupListeners();

        if (bookingId != null) {
            loadBookingDetails(bookingId);
        }
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
        // Initial setup if needed
    }

    private void setupListeners() {

        binding.modifyBookingBtn.setOnClickListener(v -> {
            listener.navigateToModifyBooking();
        });

        binding.cancelBookingBtn.setOnClickListener(v -> {
            listener.navigateToCancelBooking();
        });

        binding.contactSupportBtn.setOnClickListener(v -> {
            // Handle contact support
        });

        binding.getDirectionsBtn.setOnClickListener(v -> {
            // Handle get directions
        });
    }

    private void loadBookingDetails(String bookingId) {
        // Simulate loading booking details - in a real app this would come from a repository
        booking = findBookingById(bookingId);
        if (booking != null) {
            displayBookingDetails(booking);
        }
    }

    private Booking findBookingById(String bookingId) {
        // Simulate finding booking by ID - in a real app this would query the repository
        // For demonstration, return a sample booking
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
        binding.bookingIdText.setText("Booking ID: " + booking.getBookingId());
        binding.stationName.setText(booking.getStationName());
        binding.stationAddress.setText(booking.getStationAddress());
        binding.bookingDate.setText(booking.getDate());

        // Parse time range (e.g., "2:00 PM - 4:00 PM")
        String[] timeParts = booking.getTimeRange().split(" - ");
        if (timeParts.length == 2) {
            binding.startTime.setText(timeParts[0]);
            binding.endTime.setText(timeParts[1]);
        }

        // Calculate duration (simplified)
        binding.bookingDuration.setText("2 hours");

        // Set vehicle info (placeholder)
        binding.vehicleInfo.setText("Tesla Model 3 - ABC123");

        // Set status
        binding.bookingStatus.setText(booking.getStatusLabel());
        int color = ContextCompat.getColor(requireContext(), booking.getStatus().getColorRes());
        binding.bookingStatus.setTextColor(color);

        // Enable/disable action buttons based on booking status
        binding.modifyBookingBtn.setEnabled(booking.canModify());
        binding.cancelBookingBtn.setEnabled(booking.canCancel());
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
