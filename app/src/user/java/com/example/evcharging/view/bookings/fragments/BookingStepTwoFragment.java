package com.example.evcharging.view.bookings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.evcharging.R;
import com.example.evcharging.databinding.FragmentBookingStepTwoBinding;
import com.example.evcharging.view.bookings.BookingActionListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingStepTwoFragment extends Fragment {
    private static final String ARG_STATION_ID = "station_id";
    private static final String ARG_STATION_NAME = "station_name";

    private FragmentBookingStepTwoBinding binding;
    private BookingActionListener listener;
    private String stationId;
    private String stationName;
    private String selectedDate;
    private String selectedTime;
    private Calendar calendar = Calendar.getInstance();

    public static BookingStepTwoFragment newInstance(String stationId, String stationName) {
        BookingStepTwoFragment fragment = new BookingStepTwoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATION_ID, stationId);
        args.putString(ARG_STATION_NAME, stationName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stationId = getArguments().getString(ARG_STATION_ID);
            stationName = getArguments().getString(ARG_STATION_NAME);
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
        binding = FragmentBookingStepTwoBinding.inflate(inflater, container, false);

        initializeViews();
        setupListeners();
        updateUI();

        return binding.getRoot();
    }

    private void initializeViews() {
        binding.nextBtn.setEnabled(false);
    }

    private void setupListeners() {
        binding.selectDateBtn.setOnClickListener(v -> showDatePicker());
        binding.selectTimeBtn.setOnClickListener(v -> showTimePicker());

        binding.nextBtn.setOnClickListener(v -> {
            if (selectedDate != null && selectedTime != null) {
                listener.navigateToStepThree(stationId, stationName, selectedDate, selectedTime);
            }
        });

        binding.backBtn.setOnClickListener(v -> {
            listener.goBackToStepOne();
        });
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            selectedDate = dateFormat.format(calendar.getTime());
            binding.selectedDateText.setText(selectedDate);
            binding.selectedDateText.setVisibility(View.VISIBLE);

            checkIfCanProceed();
        });

        datePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTitleText("Select time")
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            selectedTime = timeFormat.format(calendar.getTime());
            binding.selectedTimeText.setText(selectedTime);
            binding.selectedTimeText.setVisibility(View.VISIBLE);

            checkIfCanProceed();
        });

        timePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    private void updateUI() {
        if (stationName != null) {
            binding.selectedStationText.setText(stationName);
        }
    }

    private void checkIfCanProceed() {
        binding.nextBtn.setEnabled(selectedDate != null && selectedTime != null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}