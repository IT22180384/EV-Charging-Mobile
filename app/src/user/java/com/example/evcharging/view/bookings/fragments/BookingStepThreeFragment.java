package com.example.evcharging.view.bookings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.evcharging.databinding.FragmentBookingStepThreeBinding;
import com.example.evcharging.view.bookings.BookingActionListener;

public class BookingStepThreeFragment extends Fragment {
    private static final String ARG_STATION_ID = "station_id";
    private static final String ARG_STATION_NAME = "station_name";
    private static final String ARG_DATE = "date";
    private static final String ARG_TIME = "time";

    private FragmentBookingStepThreeBinding binding;
    private BookingActionListener listener;
    private String stationId;
    private String stationName;
    private String selectedDate;
    private String selectedTime;

    public static BookingStepThreeFragment newInstance(String stationId, String stationName, String date, String time) {
        BookingStepThreeFragment fragment = new BookingStepThreeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATION_ID, stationId);
        args.putString(ARG_STATION_NAME, stationName);
        args.putString(ARG_DATE, date);
        args.putString(ARG_TIME, time);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stationId = getArguments().getString(ARG_STATION_ID);
            stationName = getArguments().getString(ARG_STATION_NAME);
            selectedDate = getArguments().getString(ARG_DATE);
            selectedTime = getArguments().getString(ARG_TIME);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingStepThreeBinding.inflate(inflater, container, false);

        setupListeners();
        updateSummary();

        return binding.getRoot();
    }

    private void setupListeners() {
        binding.confirmBtn.setOnClickListener(v -> {
            // Here you would typically make an API call to confirm the booking
            showConfirmationDialog();
        });

        binding.backBtn.setOnClickListener(v -> {
            listener.goBackToStepTwo(stationId, stationName);
        });
    }

    private void updateSummary() {
        if (stationName != null) {
            binding.summaryStationText.setText(stationName);
        }
        if (selectedDate != null) {
            binding.summaryDateText.setText(selectedDate);
        }
        if (selectedTime != null) {
            binding.summaryTimeText.setText(selectedTime);
        }

        // Calculate estimated cost (this would typically come from an API)
        binding.estimatedCostText.setText("$15.00 - $25.00");
    }

    private void showConfirmationDialog() {
        // Show confirmation and finish the booking process
        Toast.makeText(getContext(), "Booking confirmed!", Toast.LENGTH_LONG).show();

        // Use the listener to complete the booking
        listener.completeBooking();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}