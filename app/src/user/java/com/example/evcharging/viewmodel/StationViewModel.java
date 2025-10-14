package com.example.evcharging.viewmodel;

import androidx.annotation.NonNull;

import com.example.evcharging.http.Api;
import com.example.evcharging.http.ApiResponse;
import com.example.evcharging.http.HttpCallback;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.model.ChargingStation;

import java.util.ArrayList;
import java.util.List;

public class StationViewModel extends BaseViewModel {

    public interface StationsCallback {
        void onStationsLoaded(List<ChargingStation> stations);
        void onError(String errorMessage);
    }

    private final Api api;

    public StationViewModel() {
        this.api = RetrofitProvider.getInstance().create(Api.class);
    }

    public void loadChargingStations(boolean activeOnly, @NonNull StationsCallback callback) {
        api.getChargingStations(activeOnly).enqueue(new HttpCallback<ApiResponse<List<ChargingStation>>>() {
            @Override
            public void onResult(ApiResponse<List<ChargingStation>> result) {
                List<ChargingStation> data = result != null ? result.getData() : null;
                List<ChargingStation> validStations = new ArrayList<>();

                if (data != null) {
                    // Filter only active stations with valid coordinates
                    for (ChargingStation station : data) {
                        if (station != null && station.isActive() &&
                            station.getLatitude() != 0 && station.getLongitude() != 0) {
                            validStations.add(station);
                        }
                    }
                }

                callback.onStationsLoaded(validStations);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void loadNearbyStations(double latitude, double longitude, Double radiusKm, @NonNull StationsCallback callback) {
        api.getNearbyStations(latitude, longitude, radiusKm).enqueue(new HttpCallback<ApiResponse<List<ChargingStation>>>() {
            @Override
            public void onResult(ApiResponse<List<ChargingStation>> result) {
                List<ChargingStation> data = result != null ? result.getData() : null;
                List<ChargingStation> validStations = new ArrayList<>();

                if (data != null) {
                    // Filter only active stations with valid coordinates
                    for (ChargingStation station : data) {
                        if (station != null && station.isActive() &&
                            station.getLatitude() != 0 && station.getLongitude() != 0) {
                            validStations.add(station);
                        }
                    }
                }

                callback.onStationsLoaded(validStations);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}

