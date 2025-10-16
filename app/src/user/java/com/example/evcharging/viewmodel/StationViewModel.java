package com.example.evcharging.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.evcharging.application.MyApplication;
import com.example.evcharging.data.local.DBHelper;
import com.example.evcharging.http.Api;
import com.example.evcharging.http.ApiResponse;
import com.example.evcharging.http.HttpCallback;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.model.ChargingStation;
import com.example.evcharging.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StationViewModel extends BaseViewModel {

    private static final String ENDPOINT_STATIONS = "/api/stations";
    private static final String ENDPOINT_NEARBY = "/api/stations/nearby";

    public interface StationsCallback {
        void onStationsLoaded(List<ChargingStation> stations);
        void onError(String errorMessage);
    }

    private final Api api;
    private final DBHelper dbHelper;
    private final Context appContext;
    private final Gson gson;
    private final Type stationListType;

    public StationViewModel() {
        this.api = RetrofitProvider.getInstance().create(Api.class);
        MyApplication application = MyApplication.getInstance();
        appContext = application != null ? application.getApplicationContext() : null;
        dbHelper = appContext != null ? DBHelper.getInstance(appContext) : null;
        gson = new Gson();
        stationListType = new TypeToken<List<ChargingStation>>() {}.getType();
    }

    public void loadChargingStations(boolean activeOnly, @NonNull StationsCallback callback) {
        String cacheKey = "active=" + activeOnly;
        if (appContext != null && !NetworkUtils.isNetworkAvailable(appContext)) {
            if (!loadCachedStations(ENDPOINT_STATIONS, cacheKey, callback)) {
                callback.onError("No internet connection and no cached stations found.");
            }
            return;
        }
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
                    cacheStations(ENDPOINT_STATIONS, cacheKey, data);
                }

                callback.onStationsLoaded(validStations);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                if (!loadCachedStations(ENDPOINT_STATIONS, cacheKey, callback)) {
                    callback.onError(errorMessage);
                }
            }
        });
    }

    public void loadNearbyStations(double latitude, double longitude, Double radiusKm, @NonNull StationsCallback callback) {
        String cacheKey = latitude + "," + longitude + ":" + (radiusKm != null ? radiusKm : "null");
        if (appContext != null && !NetworkUtils.isNetworkAvailable(appContext)) {
            if (!loadCachedStations(ENDPOINT_NEARBY, cacheKey, callback)) {
                callback.onError("No internet connection and no cached stations found.");
            }
            return;
        }
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
                    cacheStations(ENDPOINT_NEARBY, cacheKey, data);
                }

                callback.onStationsLoaded(validStations);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                if (!loadCachedStations(ENDPOINT_NEARBY, cacheKey, callback)) {
                    callback.onError(errorMessage);
                }
            }
        });
    }

    private void cacheStations(String endpoint, String key, List<ChargingStation> stations) {
        if (dbHelper == null) {
            return;
        }
        List<ChargingStation> safeList = stations != null ? stations : new ArrayList<>();
        dbHelper.saveCachedResponse(endpoint, key, gson.toJson(safeList));
    }

    private boolean loadCachedStations(String endpoint, String key, StationsCallback callback) {
        if (dbHelper == null) {
            return false;
        }
        String cached = dbHelper.getCachedResponse(endpoint, key);
        if (cached == null) {
            return false;
        }
        List<ChargingStation> stations = gson.fromJson(cached, stationListType);
        List<ChargingStation> validStations = new ArrayList<>();
        if (stations != null) {
            for (ChargingStation station : stations) {
                if (station != null && station.isActive() &&
                        station.getLatitude() != 0 && station.getLongitude() != 0) {
                    validStations.add(station);
                }
            }
        }
        callback.onStationsLoaded(validStations);
        return true;
    }
}
