package com.example.evcharging.view.bookings.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.evcharging.R;
import com.example.evcharging.view.bookings.BookingsActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingStepTwoFragment extends Fragment {
    private static final String ARG_STATION_ID = "station_id";
    private static final String ARG_STATION_NAME = "station_name";

    private String stationId;
    private String stationName;
    private String selectedDate;
    private String selectedTime;

    private TextView selectedStationText;
    private Button selectDateBtn;
    private Button selectTimeBtn;
    private TextView selectedDateText;
    private TextView selectedTimeText;
    private Button nextBtn;
    private Button backBtn;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_step_two, container, false);

        initializeViews(view);
        setupListeners();
        updateUI();

        return view;
    }

    private void initializeViews(View view) {
        selectedStationText = view.findViewById(R.id.selectedStationText);
        selectDateBtn = view.findViewById(R.id.selectDateBtn);
        selectTimeBtn = view.findViewById(R.id.selectTimeBtn);
        selectedDateText = view.findViewById(R.id.selectedDateText);
        selectedTimeText = view.findViewById(R.id.selectedTimeText);
        nextBtn = view.findViewById(R.id.nextBtn);
        backBtn = view.findViewById(R.id.backBtn);

        nextBtn.setEnabled(false);
    }

    private void setupListeners() {
        selectDateBtn.setOnClickListener(v -> showDatePicker());
        selectTimeBtn.setOnClickListener(v -> showTimePicker());

        nextBtn.setOnClickListener(v -> {
            if (selectedDate != null && selectedTime != null && getActivity() instanceof BookingsActivity) {
                ((BookingsActivity) getActivity()).goToStepThree(stationId, stationName, selectedDate, selectedTime);
            }
        });

        backBtn.setOnClickListener(v -> {
            if (getActivity() instanceof BookingsActivity) {
                ((BookingsActivity) getActivity()).goBackToStepOne();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                selectedDate = dateFormat.format(calendar.getTime());
                selectedDateText.setText(selectedDate);
                selectedDateText.setVisibility(View.VISIBLE);

                checkIfCanProceed();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            getContext(),
            (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                selectedTime = timeFormat.format(calendar.getTime());
                selectedTimeText.setText(selectedTime);
                selectedTimeText.setVisibility(View.VISIBLE);

                checkIfCanProceed();
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        );

        timePickerDialog.show();
    }

    private void updateUI() {
        if (stationName != null) {
            selectedStationText.setText(stationName);
        }
    }

    private void checkIfCanProceed() {
        nextBtn.setEnabled(selectedDate != null && selectedTime != null);
    }
}
