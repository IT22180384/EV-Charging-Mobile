package com.example.evcharging.view.bookings.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.evcharging.R;
import com.example.evcharging.view.bookings.BookingsActivity;

public class BookingStepThreeFragment extends Fragment {
    private static final String ARG_STATION_ID = "station_id";
    private static final String ARG_STATION_NAME = "station_name";
    private static final String ARG_DATE = "date";
    private static final String ARG_TIME = "time";

    private String stationId;
    private String stationName;
    private String selectedDate;
    private String selectedTime;

    private TextView summaryStationText;
    private TextView summaryDateText;
    private TextView summaryTimeText;
    private TextView estimatedCostText;
    private Button confirmBtn;
    private Button backBtn;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_step_three, container, false);

        initializeViews(view);
        setupListeners();
        updateSummary();

        return view;
    }

    private void initializeViews(View view) {
        summaryStationText = view.findViewById(R.id.summaryStationText);
        summaryDateText = view.findViewById(R.id.summaryDateText);
        summaryTimeText = view.findViewById(R.id.summaryTimeText);
        estimatedCostText = view.findViewById(R.id.estimatedCostText);
        confirmBtn = view.findViewById(R.id.confirmBtn);
        backBtn = view.findViewById(R.id.backBtn);
    }

    private void setupListeners() {
        confirmBtn.setOnClickListener(v -> {
            // Here you would typically make an API call to confirm the booking
            showConfirmationDialog();
        });

        backBtn.setOnClickListener(v -> {
            if (getActivity() instanceof BookingsActivity) {
                ((BookingsActivity) getActivity()).goBackToStepTwo(stationId, stationName);
            }
        });
    }

    private void updateSummary() {
        if (stationName != null) {
            summaryStationText.setText(stationName);
        }
        if (selectedDate != null) {
            summaryDateText.setText(selectedDate);
        }
        if (selectedTime != null) {
            summaryTimeText.setText(selectedTime);
        }

        // Calculate estimated cost (this would typically come from an API)
        estimatedCostText.setText("$15.00 - $25.00");
    }

    private void showConfirmationDialog() {
        // Show confirmation and finish the booking process
        Toast.makeText(getContext(), "Booking confirmed!", Toast.LENGTH_LONG).show();

        // In a real app, you would navigate to a confirmation screen or back to the main activity
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
