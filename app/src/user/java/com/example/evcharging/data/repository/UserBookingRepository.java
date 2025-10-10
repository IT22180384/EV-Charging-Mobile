package com.example.evcharging.data.repository;

import androidx.annotation.NonNull;

import com.example.evcharging.http.Api;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.http.dto.BookingSessionResponse;
import com.example.evcharging.http.dto.ReservationCreateRequest;
import com.example.evcharging.http.dto.ReservationResponse;
import com.example.evcharging.http.dto.ReservationUpdateRequest;
import com.example.evcharging.http.dto.StationDetailResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserBookingRepository {

    public interface ReservationCallback {
        void onSuccess(ReservationResponse response);
        void onError(String errorMessage);
    }

    public interface ReservationActionCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public interface BookingSessionsCallback {
        void onSuccess(List<BookingSessionResponse> sessions);
        void onError(String errorMessage);
    }

    public interface StationDetailCallback {
        void onSuccess(StationDetailResponse station);
        void onError(String errorMessage);
    }

    private final Api api;

    public UserBookingRepository() {
        api = RetrofitProvider.getInstance().create(Api.class);
    }

    public void createReservation(ReservationCreateRequest request, ReservationCallback callback) {
        api.createReservation(request).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationResponse> call, @NonNull Response<ReservationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to create reservation");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationResponse> call, @NonNull Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    public void getPendingBookings(String userId, BookingSessionsCallback callback) {
        api.getUserPendingBookings(userId).enqueue(createSessionCallback(callback));
    }

    public void getCompletedBookings(String userId, BookingSessionsCallback callback) {
        api.getUserCompletedBookings(userId).enqueue(createSessionCallback(callback));
    }

    public void updateReservation(String reservationId, ReservationUpdateRequest request, ReservationCallback callback) {
        api.updateReservation(reservationId, request).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReservationResponse> call,
                                   @NonNull Response<ReservationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update reservation");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReservationResponse> call, @NonNull Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    public void cancelReservation(String reservationId, ReservationActionCallback callback) {
        android.util.Log.d("BookingRepo", "cancelReservation called with ID: " + reservationId);
        api.cancelReservation(reservationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                android.util.Log.d("BookingRepo", "Cancel response code: " + response.code());
                android.util.Log.d("BookingRepo", "Cancel response message: " + response.message());
                if (response.isSuccessful()) {
                    android.util.Log.d("BookingRepo", "Cancel successful");
                    callback.onSuccess();
                } else {
                    android.util.Log.e("BookingRepo", "Cancel failed with code: " + response.code());
                    callback.onError("Failed to cancel reservation. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                android.util.Log.e("BookingRepo", "Cancel request failed", t);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    public void getStationDetail(String stationId, StationDetailCallback callback) {
        api.getStationDetail(stationId).enqueue(new Callback<com.example.evcharging.http.ApiResponse<StationDetailResponse>>() {
            @Override
            public void onResponse(@NonNull Call<com.example.evcharging.http.ApiResponse<StationDetailResponse>> call,
                                   @NonNull Response<com.example.evcharging.http.ApiResponse<StationDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()
                        && response.body().getData() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to fetch station detail");
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.example.evcharging.http.ApiResponse<StationDetailResponse>> call,
                                  @NonNull Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    private Callback<List<BookingSessionResponse>> createSessionCallback(BookingSessionsCallback callback) {
        return new Callback<List<BookingSessionResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<BookingSessionResponse>> call,
                                   @NonNull Response<List<BookingSessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load bookings");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BookingSessionResponse>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        };
    }
}
