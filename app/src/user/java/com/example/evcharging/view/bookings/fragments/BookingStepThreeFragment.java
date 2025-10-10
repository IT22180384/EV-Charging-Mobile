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
import androidx.lifecycle.ViewModelProvider;

import com.example.evcharging.databinding.FragmentBookingStepThreeBinding;
import com.example.evcharging.http.dto.ReservationResponse;
import com.example.evcharging.data.TokenManager;
import com.example.evcharging.view.bookings.ReservationConfirmationActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.evcharging.view.bookings.BookingActionListener;
import com.example.evcharging.viewmodel.BookingViewModel;

public class BookingStepThreeFragment extends Fragment implements BookingViewModel.BookingCallback {
    private static final String ARG_STATION_ID = "station_id";
    private static final String ARG_STATION_NAME = "station_name";
    private static final String ARG_DATE = "date";
    private static final String ARG_TIME = "time";

    private FragmentBookingStepThreeBinding binding;
    private BookingViewModel viewModel;
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
        viewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingStepThreeBinding.inflate(inflater, container, false);

        setupListeners();
        updateSummary();

        return binding.getRoot();
    }

    private void setupListeners() {
        binding.confirmBtn.setOnClickListener(v -> createReservation());

        binding.backBtn.setOnClickListener(v -> listener.goBackToStepTwo(stationId, stationName));
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
    }

    private void createReservation() {
        String uid = TokenManager.getUserId(requireContext().getApplicationContext());
        if (uid == null || uid.isEmpty()) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse selectedDate (e.g., "Dec 15, 2024") and selectedTime (e.g., "1 - 2")
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            Date date = dateFormat.parse(selectedDate);

            String startHourPart = selectedTime.split("-")[0].trim();
            int hour = Integer.parseInt(startHourPart);
            if (hour >= 1 && hour <= 6) hour += 12; // 1..6 -> 13..18

            Calendar startCal = Calendar.getInstance();
            startCal.setTime(date);
            startCal.set(Calendar.HOUR_OF_DAY, hour);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(startCal.getTime());
            endCal.add(Calendar.HOUR_OF_DAY, 1);

            // Format as ISO 8601 string (without timezone)
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            String startIso = isoFormat.format(startCal.getTime());
            String endIso = isoFormat.format(endCal.getTime());

            binding.confirmBtn.setEnabled(false);
            viewModel.createReservation(uid, stationId, startIso, endIso, null, this);

        } catch (ParseException | NumberFormatException ex) {
            Toast.makeText(getContext(), "Invalid date/time selection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReservationSuccess(ReservationResponse response) {
        if (binding != null) {
            binding.confirmBtn.setEnabled(true);
        }
        android.util.Log.d("ReservationCreate",
                "Response: ID=" + response.id + ", OperatorId=" + response.operatorId + ", BookingId=" + response.bookingId);
        navigateToConfirmation(response);
    }

    @Override
    public void onReservationError(String errorMessage) {
        if (binding != null) {
            binding.confirmBtn.setEnabled(true);
        }
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void navigateToConfirmation(ReservationResponse res) {
        // Verify we have all required data
        if (res.operatorId == null || res.operatorId.isEmpty() || res.bookingId == null || res.bookingId.isEmpty()) {
            android.util.Log.w("ReservationCreate", "Missing operatorId or bookingId in response");
            Toast.makeText(getContext(), "Reservation created but missing some details", Toast.LENGTH_SHORT).show();
        }
        
        // Navigate to confirmation screen with QR and identifiers
        android.content.Intent i = new android.content.Intent(getContext(), ReservationConfirmationActivity.class);
        i.putExtra(ReservationConfirmationActivity.EXTRA_STATION_NAME, stationName);
        i.putExtra(ReservationConfirmationActivity.EXTRA_DATE, selectedDate);
        i.putExtra(ReservationConfirmationActivity.EXTRA_TIME, selectedTime);
        i.putExtra(ReservationConfirmationActivity.EXTRA_RES_ID, res.id);
        i.putExtra(ReservationConfirmationActivity.EXTRA_QR, res.qrCode);
        i.putExtra(ReservationConfirmationActivity.EXTRA_OPERATOR_ID, res.operatorId != null ? res.operatorId : "");
        i.putExtra(ReservationConfirmationActivity.EXTRA_BOOKING_ID, res.bookingId != null ? res.bookingId : "");
        startActivity(i);
        // Complete flow
        listener.completeBooking();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}