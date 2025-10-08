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
import com.example.evcharging.http.Api;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.http.dto.ReservationCreateRequest;
import com.example.evcharging.http.dto.ReservationResponse;
import com.example.evcharging.data.TokenManager;
import com.example.evcharging.view.bookings.ReservationConfirmationActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
            createReservation();
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

    }

    private void createReservation() {
        String uid = TokenManager.getUserId(requireContext().getApplicationContext());
        if (uid == null || uid.isEmpty()) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Parse selectedDate (e.g., "Dec 15, 2024") and selectedTime (e.g., "1 - 2")
            DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            LocalDate date = LocalDate.parse(selectedDate, df);
            String startHourPart = selectedTime.split("-")[0].trim();
            int hour = Integer.parseInt(startHourPart);
            if (hour >= 1 && hour <= 6) hour += 12; // 1..6 -> 13..18
            LocalTime start = LocalTime.of(hour, 0);
            LocalTime end = start.plusHours(1);
            LocalDateTime startLdt = LocalDateTime.of(date, start);
            LocalDateTime endLdt = LocalDateTime.of(date, end);

            // Convert to UTC ISO strings
            java.time.ZonedDateTime startUtc = startLdt.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            java.time.ZonedDateTime endUtc = endLdt.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            String startIso = startUtc.toInstant().toString();
            String endIso = endUtc.toInstant().toString();

            ReservationCreateRequest req = new ReservationCreateRequest(uid, stationId, startIso, endIso, null);
            Api api = RetrofitProvider.getInstance().create(Api.class);
            binding.confirmBtn.setEnabled(false);
            api.createReservation(req).enqueue(new Callback<ReservationResponse>() {
                @Override public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                    binding.confirmBtn.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        ReservationResponse res = response.body();
                        // Navigate to confirmation screen with QR
                        android.content.Intent i = new android.content.Intent(getContext(), ReservationConfirmationActivity.class);
                        i.putExtra(ReservationConfirmationActivity.EXTRA_STATION_NAME, stationName);
                        i.putExtra(ReservationConfirmationActivity.EXTRA_DATE, selectedDate);
                        i.putExtra(ReservationConfirmationActivity.EXTRA_TIME, selectedTime);
                        i.putExtra(ReservationConfirmationActivity.EXTRA_RES_ID, res.id);
                        i.putExtra(ReservationConfirmationActivity.EXTRA_QR, res.qrCode);
                        i.putExtra(ReservationConfirmationActivity.EXTRA_OPERATOR_ID, res.operatorId);
                        startActivity(i);
                        // Complete flow
                        listener.completeBooking();
                    } else {
                        Toast.makeText(getContext(), "Failed to create reservation", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<ReservationResponse> call, Throwable t) {
                    binding.confirmBtn.setEnabled(true);
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (DateTimeParseException | NumberFormatException ex) {
            Toast.makeText(getContext(), "Invalid date/time selection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
