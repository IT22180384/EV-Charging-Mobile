package com.example.evcharging.view.bookings.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import com.example.evcharging.databinding.FragmentModifyBookingBinding;
import com.example.evcharging.http.dto.ReservationResponse;
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.BookingActionListener;
import com.example.evcharging.viewmodel.BookingViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ModifyBookingFragment extends Fragment {
    private FragmentModifyBookingBinding binding;
    private BookingActionListener listener;
    private Booking booking;
    private String bookingId;
    private BookingViewModel viewModel;
    private Calendar selectedDate;
    private Date selectedStartDateTime;
    private Date selectedEndDateTime;

    public static ModifyBookingFragment newInstance(Booking booking) {
        ModifyBookingFragment fragment = new ModifyBookingFragment();
        Bundle args = new Bundle();
        if (booking != null) {
            args.putString("booking_id", booking.getBookingId());
            args.putString("reservation_id", booking.getReservationId());
            args.putString("station_id", booking.getStationId());
            args.putString("station_name", booking.getStationName());
            args.putString("station_address", booking.getStationAddress());
            args.putString("date", booking.getDate());
            args.putString("time_range", booking.getTimeRange());
            args.putString("slot", booking.getSlot());
            args.putString("status", booking.getStatus().name());
            args.putString("status_label", booking.getStatusLabel());
            args.putBoolean("can_modify", booking.canModify());
            args.putBoolean("can_cancel", booking.canCancel());
            args.putString("start_time", booking.getStartTimeIso());
            args.putString("end_time", booking.getEndTimeIso());
        }
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

        viewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);

        // Load booking from arguments
        if (getArguments() != null) {
            loadBookingFromArguments();
        }

        initializeViews();
        setupListeners();

        if (booking != null) {
            displayBookingDetails(booking);
        } else if (bookingId != null) {
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

    private void loadBookingFromArguments() {
        Bundle args = getArguments();
        if (args != null) {
            String id = args.getString("booking_id");
            String bookingIdValue = args.getString("booking_id");
            String reservationId = args.getString("reservation_id");
            String stationId = args.getString("station_id");
            String stationName = args.getString("station_name");
            String stationAddress = args.getString("station_address");
            String date = args.getString("date");
            String timeRange = args.getString("time_range");
            String slot = args.getString("slot");
            String statusStr = args.getString("status");
            String statusLabel = args.getString("status_label");
            boolean canModify = args.getBoolean("can_modify", false);
            boolean canCancel = args.getBoolean("can_cancel", false);
            String startTime = args.getString("start_time");
            String endTime = args.getString("end_time");

            Booking.Status status = Booking.Status.PENDING;
            if (statusStr != null) {
                try {
                    status = Booking.Status.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    // Keep default PENDING status
                }
            }

            booking = new Booking(
                    id,
                    bookingIdValue,
                    reservationId,
                    stationId,
                    stationName,
                    stationAddress,
                    date,
                    timeRange,
                    slot,
                    status,
                    statusLabel,
                    canModify,
                    canCancel,
                    startTime,
                    endTime
            );
            this.bookingId = id;
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


    private void displayBookingDetails(Booking booking) {
        binding.textCurrentBookingTitle.setText(booking.getStationName());
        binding.textCurrentDate.setText(booking.getDate());
        binding.textCurrentTime.setText(booking.getTimeRange());

        // Set initial values for modification
        binding.textSelectedDate.setText(booking.getDate());
        binding.textSelectedTime.setText(booking.getTimeRange());
    }

    private void loadBookingDetails(String bookingId) {
        if (booking == null) {
            Toast.makeText(getContext(), "Unable to load booking details", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            binding.textSelectedDate.setText(format.format(selectedDate.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePicker() {
        if (selectedDate == null) {
            Toast.makeText(getContext(), "Please select a date first", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            Calendar startCal = (Calendar) selectedDate.clone();
            startCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startCal.set(Calendar.MINUTE, minute);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            selectedStartDateTime = startCal.getTime();
            Calendar endCal = (Calendar) startCal.clone();
            endCal.add(Calendar.HOUR_OF_DAY, 1);
            selectedEndDateTime = endCal.getTime();
            SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.getDefault());
            String startStr = format.format(selectedStartDateTime);
            String endStr = format.format(selectedEndDateTime);
            binding.textSelectedTime.setText(startStr + " - " + endStr);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        dialog.show();
    }

    private void saveModifications() {
        if (selectedStartDateTime == null || selectedEndDateTime == null) {
            Toast.makeText(getContext(), "Please select both date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (booking == null) {
            Toast.makeText(getContext(), "Booking details not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault());
        apiFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startTime = apiFormat.format(selectedStartDateTime);
        String endTime = apiFormat.format(selectedEndDateTime);

        String reservationId = booking.getReservationId();
        String chargingStationId = booking.getStationId();

        viewModel.updateReservation(reservationId, startTime, endTime, null, null, chargingStationId, new BookingViewModel.ReservationUpdateCallback() {
            @Override
            public void onUpdateSuccess(ReservationResponse response) {
                Toast.makeText(getContext(), "Booking updated successfully", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                if (listener != null) {
                    listener.onBookingModified(bookingId);
                }
            }

            @Override
            public void onUpdateError(String errorMessage) {
                Toast.makeText(getContext(), "Error updating booking: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
        if (getView() != null) {
            loadBookingDetails(bookingId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}