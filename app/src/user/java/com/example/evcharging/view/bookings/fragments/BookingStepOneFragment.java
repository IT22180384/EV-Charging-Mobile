package com.example.evcharging.view.bookings.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evcharging.R;
import com.example.evcharging.view.bookings.BookingsActivity;

public class BookingStepOneFragment extends Fragment {
    private RecyclerView stationRecycler;
    private EditText searchStations;
    private Button nextBtn;
    private Button listViewBtn;
    private Button mapViewBtn;
    private String selectedStationId;
    private String selectedStationName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_step_one, container, false);

        initializeViews(view);
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        stationRecycler = view.findViewById(R.id.stationRecycler);
        searchStations = view.findViewById(R.id.searchStations);
        nextBtn = view.findViewById(R.id.nextBtn);
        listViewBtn = view.findViewById(R.id.listViewBtn);
        mapViewBtn = view.findViewById(R.id.mapViewBtn);

        // Setup RecyclerView
        stationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set initial state
        nextBtn.setEnabled(false);
        listViewBtn.setTextColor(getResources().getColor(R.color.apple_secondary));
        mapViewBtn.setTextColor(getResources().getColor(R.color.apple_gray_dark));
    }

    private void setupListeners() {
        nextBtn.setOnClickListener(v -> {
            if (selectedStationId != null && getActivity() instanceof BookingsActivity) {
                ((BookingsActivity) getActivity()).goToStepTwo(selectedStationId, selectedStationName);
            }
        });

        listViewBtn.setOnClickListener(v -> {
            listViewBtn.setTextColor(getResources().getColor(R.color.apple_secondary));
            mapViewBtn.setTextColor(getResources().getColor(R.color.apple_gray_dark));
            // Show list view
        });

        mapViewBtn.setOnClickListener(v -> {
            mapViewBtn.setTextColor(getResources().getColor(R.color.apple_secondary));
            listViewBtn.setTextColor(getResources().getColor(R.color.apple_gray_dark));
            // Show map view
        });

        searchStations.addTextChangedListener(new TextWatcher() {
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
        nextBtn.setEnabled(true);
    }
}
