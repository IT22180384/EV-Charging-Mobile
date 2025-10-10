package com.example.evcharging.view.bookings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.evcharging.data.TokenManager;
import com.example.evcharging.databinding.FragmentBookingsListBinding;
import com.example.evcharging.http.dto.ReservationResponse;
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.BookingActionListener;
import com.example.evcharging.view.bookings.adapters.BookingsListAdapter;
import com.example.evcharging.viewmodel.BookingViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UpcomingBookingsFragment extends Fragment implements BookingViewModel.BookingsCallback {
    private FragmentBookingsListBinding binding;
    private BookingActionListener listener;
    private BookingsListAdapter adapter;
    private BookingViewModel viewModel;

    private final java.text.SimpleDateFormat isoWithMs = createParser("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private final java.text.SimpleDateFormat isoWithZone = createParser("yyyy-MM-dd'T'HH:mm:ssX");
    private final java.text.SimpleDateFormat isoWithoutZone = createParser("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BookingActionListener) {
            listener = (BookingActionListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(BookingViewModel.class);
        adapter = new BookingsListAdapter(new ArrayList<>(), new BookingsListAdapter.OnBookingActionListener() {
            @Override
            public void onBookingSelected(@NonNull Booking booking) {
                if (listener != null) {
                    listener.navigateToBookingDetails();
                }
            }

            @Override
            public void onModify(@NonNull Booking booking) {
                showModifyDialog(booking);
            }

            @Override
            public void onCancel(@NonNull Booking booking) {
                confirmCancellation(booking);
            }
        });
        binding.recyclerBookings.setAdapter(adapter);
        loadUpcomingBookings();
    }

    private void loadUpcomingBookings() {
        if (!isAdded()) {
            return;
        }
        String userId = TokenManager.getUserId(requireContext().getApplicationContext());
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(getContext(), "Please login to view bookings", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.getPendingBookings(userId, this);
    }

    @Override
    public void onBookingsLoaded(List<Booking> bookings) {
        if (binding != null && adapter != null) {
            adapter.updateBookings(bookings);
        }
    }

    @Override
    public void onError(String errorMessage) {
        if (getContext() != null) {
            Toast.makeText(getContext(),
                    errorMessage != null ? errorMessage : "Failed to load bookings",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showModifyDialog(Booking booking) {
        if (!isAdded()) return;
        if (TextUtils.isEmpty(booking.getReservationId())) {
            Toast.makeText(getContext(), "Reservation reference unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        Date currentStart = parseIso(booking.getStartTimeIso());
        long initialSelection = currentStart != null ? currentStart.getTime() : MaterialDatePicker.todayInUtcMilliseconds();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select reservation date")
                .setSelection(initialSelection)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection == null) return;
            Calendar selectedDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
            selectedDate.setTimeInMillis(selection);
            showTimePicker(booking, selectedDate);
        });

        datePicker.show(getChildFragmentManager(), "modify_reservation_date");
    }

    private void showTimePicker(Booking booking, Calendar selectedDate) {
        if (!isAdded()) return;
        Date currentStart = parseIso(booking.getStartTimeIso());
        int initialHour = currentStart != null ? getField(currentStart, Calendar.HOUR_OF_DAY) : 9;
        int initialMinute = currentStart != null ? getField(currentStart, Calendar.MINUTE) : 0;

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(initialHour)
                .setMinute(initialMinute)
                .setTitleText("Select start time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            Calendar startCal = (Calendar) selectedDate.clone();
            startCal.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            startCal.set(Calendar.MINUTE, timePicker.getMinute());
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);

            Calendar endCal = (Calendar) startCal.clone();
            endCal.add(Calendar.HOUR_OF_DAY, 1);

            submitReservationUpdate(booking, formatIsoLocal(startCal), formatIsoLocal(endCal));
        });

        timePicker.show(getChildFragmentManager(), "modify_reservation_time");
    }

    private void submitReservationUpdate(Booking booking, String startIso, String endIso) {
        viewModel.updateReservation(booking.getReservationId(), startIso, endIso, null, null,
                new BookingViewModel.ReservationUpdateCallback() {
                    @Override
                    public void onUpdateSuccess(ReservationResponse response) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(), "Reservation updated", Toast.LENGTH_SHORT).show();
                        loadUpcomingBookings();
                    }

                    @Override
                    public void onUpdateError(String errorMessage) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                errorMessage != null ? errorMessage : "Failed to update reservation",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void confirmCancellation(Booking booking) {
        if (!isAdded()) return;
        if (TextUtils.isEmpty(booking.getReservationId())) {
            Toast.makeText(getContext(), "Reservation reference unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancel reservation?")
                .setMessage("Do you want to cancel your reservation at " + booking.getStationName() +
                        " on " + booking.getDate() + " at " + booking.getTimeRange() + "?")
                .setPositiveButton("Cancel reservation", (dialog, which) -> performCancellation(booking))
                .setNegativeButton("Keep reservation", null)
                .show();
    }

    private void performCancellation(Booking booking) {
        viewModel.cancelReservation(booking.getReservationId(), new BookingViewModel.ReservationActionCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Reservation cancelled", Toast.LENGTH_SHORT).show();
                loadUpcomingBookings();
            }

            @Override
            public void onError(String errorMessage) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        errorMessage != null ? errorMessage : "Failed to cancel reservation",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Date parseIso(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return isoWithMs.parse(value);
        } catch (Exception ignored) {
        }
        try {
            return isoWithZone.parse(value);
        } catch (Exception ignored) {
        }
        try {
            return isoWithoutZone.parse(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private java.text.SimpleDateFormat createParser(String pattern) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(pattern, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }

    private String formatIsoLocal(Calendar calendar) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getDefault());
        return format.format(calendar.getTime());
    }

    private int getField(Date date, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(field);
    }
}
