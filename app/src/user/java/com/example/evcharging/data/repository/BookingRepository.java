package com.example.evcharging.repository;

import com.example.evcharging.http.Api;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.http.dto.ReservationCreateRequest;
import com.example.evcharging.http.dto.ReservationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {

    public interface ReservationCallback {
        void onSuccess(ReservationResponse response);
        void onError(String errorMessage);
    }

    private Api api;

    public BookingRepository() {
        api = RetrofitProvider.getInstance().create(Api.class);
    }

    public void createReservation(ReservationCreateRequest request, ReservationCallback callback) {
        api.createReservation(request).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to create reservation");
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }
}