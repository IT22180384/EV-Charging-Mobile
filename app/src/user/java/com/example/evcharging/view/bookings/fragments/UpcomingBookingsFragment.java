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
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.BookingActionListener;
import com.example.evcharging.view.bookings.adapters.BookingsListAdapter;
import com.example.evcharging.viewmodel.BookingViewModel;

import java.util.ArrayList;
import java.util.List;

public class UpcomingBookingsFragment extends Fragment implements BookingViewModel.BookingsCallback {
    private FragmentBookingsListBinding binding;
    private BookingActionListener listener;
    private BookingsListAdapter adapter;
    private BookingViewModel viewModel;

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
                    listener.navigateToBookingDetails(booking);
                }
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

    public void refreshBookings() {
        loadUpcomingBookings();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh bookings when returning to this fragment
        refreshBookings();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
