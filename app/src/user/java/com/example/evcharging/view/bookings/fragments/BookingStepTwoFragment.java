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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private LocalDate selectedLocalDate;
    private LocalTime selectedLocalTime;

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
        binding.dateCard.setOnClickListener(v -> showDatePicker());

        // Time slot chip selection
        binding.chipGroupTimeSlots.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId != View.NO_ID) {
                    Chip chip = group.findViewById(checkedId);
                    if (chip != null) {
                        selectedTime = chip.getText().toString();
                        binding.selectedTimeText.setText(selectedTime);
                        binding.selectedTimeText.setVisibility(View.VISIBLE);
                        checkIfCanProceed();
                    }
                }
            }
        });

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
            selectedLocalDate = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
            selectedDate = selectedLocalDate.format(dateFormatter);
            binding.selectedDateText.setText(selectedDate);
            binding.selectedDateText.setVisibility(View.VISIBLE);

            checkIfCanProceed();
        });

        datePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    // Time selection now handled by chips; picker retained for reference but unused

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
