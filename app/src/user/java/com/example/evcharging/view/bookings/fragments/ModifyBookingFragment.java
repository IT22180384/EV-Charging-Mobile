package com.example.evcharging.view.bookings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.evcharging.databinding.FragmentModifyBookingBinding;
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.BookingActionListener;

public class ModifyBookingFragment extends Fragment {
    private FragmentModifyBookingBinding binding;
    private BookingActionListener listener;
    private Booking booking;
    private String bookingId;

    public static ModifyBookingFragment newInstance() {
        ModifyBookingFragment fragment = new ModifyBookingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentModifyBookingBinding.inflate(inflater, container, false);
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
        // Initialize date and time pickers with current values
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.btnSaveChanges.setOnClickListener(v -> saveModifications());

        binding.btnCancel.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());

        binding.btnSelectTime.setOnClickListener(v -> showTimePicker());
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
        binding.textCurrentBookingTitle.setText(booking.getTitle());
        binding.textCurrentDate.setText(booking.getDate());
        binding.textCurrentTime.setText(booking.getTimeRange());

        // Set initial values for modification
        binding.textSelectedDate.setText(booking.getDate());
        binding.textSelectedTime.setText(booking.getTimeRange());
    }

    private void showDatePicker() {
        // Implement date picker dialog
        Toast.makeText(getContext(), "Date picker would open here", Toast.LENGTH_SHORT).show();
    }

    private void showTimePicker() {
        // Implement time picker dialog
        Toast.makeText(getContext(), "Time picker would open here", Toast.LENGTH_SHORT).show();
    }

    private void saveModifications() {
        // Validate and save modifications
        String newDate = binding.textSelectedDate.getText().toString();
        String newTime = binding.textSelectedTime.getText().toString();

        // In a real app, this would call the repository to update the booking
        if (listener != null) {
            listener.onBookingModified(bookingId);
            Toast.makeText(getContext(), "Booking modified successfully", Toast.LENGTH_SHORT).show();
        }
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