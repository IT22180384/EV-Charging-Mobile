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
import com.example.evcharging.http.ApiResponse;
import com.example.evcharging.http.HttpCallback;
import com.example.evcharging.http.Api;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.model.ChargingStation;
import com.example.evcharging.view.bookings.adapters.StationAdapter;

import java.util.ArrayList;
import java.util.List;
import com.example.evcharging.view.bookings.BookingActionListener;

public class BookingStepOneFragment extends Fragment implements StationAdapter.OnStationClickListener {
    private FragmentBookingStepOneBinding binding;
    private BookingActionListener listener;
    private String selectedStationId;
    private String selectedStationName;
    private StationAdapter adapter;
    private final List<ChargingStation> allStations = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingStepOneBinding.inflate(inflater, container, false);

        initializeViews();
        loadStations();
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
        adapter = new StationAdapter(this);
        binding.stationRecycler.setAdapter(adapter);

        binding.nextBtn.setEnabled(false);

        binding.listViewBtn.setTextColor(getResources().getColor(R.color.apple_secondary));
        binding.mapViewBtn.setTextColor(getResources().getColor(R.color.apple_gray_dark));
    }
    
    @Override
    public void onStationClick(ChargingStation station) {
        if (station != null) {
            onStationSelected(station.getId(), station.getName());
        }
    }

    private void loadStations() {
        Api api = RetrofitProvider.getInstance().create(Api.class);
        api.getChargingStations(true).enqueue(new HttpCallback<ApiResponse<List<ChargingStation>>>() {
            @Override
            public void onResult(ApiResponse<List<ChargingStation>> result) {
                List<ChargingStation> data = result != null ? result.getData() : null;
                allStations.clear();
                if (data != null) {
                    // Safety: ensure only active stations
                    for (ChargingStation s : data) {
                        if (s != null && s.isActive()) {
                            allStations.add(s);
                        }
                    }
                }
                adapter.submitList(allStations);
            }

            @Override
            public void onError(String errorMessage) {
                // Optionally show a toast/snackbar; for now, keep silent
            }
        });
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
                String q = s != null ? s.toString().trim().toLowerCase() : "";
                if (q.isEmpty()) {
                    adapter.submitList(allStations);
                } else {
                    List<ChargingStation> filtered = new ArrayList<>();
                    for (ChargingStation st : allStations) {
                        if (st == null) continue;
                        String name = st.getName() != null ? st.getName().toLowerCase() : "";
                        String addr = st.getAddress() != null ? st.getAddress().toLowerCase() : "";
                        if (name.contains(q) || addr.contains(q)) {
                            filtered.add(st);
                        }
                    }
                    adapter.submitList(filtered);
                }
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
