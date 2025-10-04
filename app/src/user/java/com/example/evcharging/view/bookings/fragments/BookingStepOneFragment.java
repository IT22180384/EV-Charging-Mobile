package com.example.evcharging.view.bookings.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.evcharging.R;
import com.example.evcharging.databinding.FragmentBookingStepOneBinding;
import com.example.evcharging.view.bookings.BookingActionListener;

public class BookingStepOneFragment extends Fragment {
    private FragmentBookingStepOneBinding binding;
    private BookingActionListener listener;
    private String selectedStationId;
    private String selectedStationName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingStepOneBinding.inflate(inflater, container, false);

        initializeViews();
        setupListeners();

        return binding.getRoot();
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

    private void initializeViews() {
        // Setup RecyclerView
        binding.stationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.listViewBtn.setTextColor(getResources().getColor(R.color.apple_secondary));
        binding.mapViewBtn.setTextColor(getResources().getColor(R.color.apple_gray_dark));
    }

    private void setupListeners() {
        binding.nextBtn.setOnClickListener(v -> {
            listener.navigateToStepTwo(selectedStationId, selectedStationName);
        });

        binding.listViewBtn.setOnClickListener(v -> {
            binding.listViewBtn.setTextColor(getResources().getColor(R.color.apple_secondary));
            binding.mapViewBtn.setTextColor(getResources().getColor(R.color.apple_gray_dark));
            // Show list view
        });

        binding.mapViewBtn.setOnClickListener(v -> {
            binding.mapViewBtn.setTextColor(getResources().getColor(R.color.apple_secondary));
            binding.listViewBtn.setTextColor(getResources().getColor(R.color.apple_gray_dark));
            // Show map view
        });

        binding.searchStations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter stations based on search
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void onStationSelected(String stationId, String stationName) {
        this.selectedStationId = stationId;
        this.selectedStationName = stationName;
        binding.nextBtn.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}