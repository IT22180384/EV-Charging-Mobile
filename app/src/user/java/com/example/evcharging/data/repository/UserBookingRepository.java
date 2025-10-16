package com.example.evcharging.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.evcharging.application.MyApplication;
import com.example.evcharging.data.local.DBHelper;
import com.example.evcharging.data.sync.OfflineSyncManager;
import com.example.evcharging.http.Api;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.http.dto.BookingSessionResponse;
import com.example.evcharging.http.dto.ReservationCreateRequest;
import com.example.evcharging.http.dto.ReservationResponse;
import com.example.evcharging.http.dto.ReservationUpdateRequest;
import com.example.evcharging.http.dto.StationDetailResponse;
import com.example.evcharging.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserBookingRepository {

    private static final String ENDPOINT_RESERVATION = "/api/reservation";
    private static final String ENDPOINT_PENDING_BOOKINGS = "/api/reservation/user/bookings/pending";
    private static final String ENDPOINT_COMPLETED_BOOKINGS = "/api/reservation/user/bookings/completed";

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
    private final DBHelper dbHelper;
    private final Context appContext;
    private final Gson gson;
    private final Type sessionListType;

    public UserBookingRepository() {
        api = RetrofitProvider.getInstance().create(Api.class);
        MyApplication application = MyApplication.getInstance();
        appContext = application != null ? application.getApplicationContext() : null;
        dbHelper = appContext != null ? DBHelper.getInstance(appContext) : null;
        gson = new Gson();
        sessionListType = new TypeToken<List<BookingSessionResponse>>() {}.getType();
    }

    public void createReservation(ReservationCreateRequest request, ReservationCallback callback) {
        if (appContext != null && !NetworkUtils.isNetworkAvailable(appContext)) {
            queuePendingRequest("POST", ENDPOINT_RESERVATION, request);
            callback.onError("No internet connection. Reservation saved locally and will sync once online.");
            return;
        }
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
                queuePendingRequest("POST", ENDPOINT_RESERVATION, request);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error. Request saved and will retry.");
            }
        });
    }

    public void getPendingBookings(String userId, BookingSessionsCallback callback) {
        if (appContext != null && !NetworkUtils.isNetworkAvailable(appContext)) {
            loadCachedSessions(ENDPOINT_PENDING_BOOKINGS, userId, callback);
            return;
        }
        api.getUserPendingBookings(userId).enqueue(createSessionCallback(callback, ENDPOINT_PENDING_BOOKINGS, userId));
    }

    public void getCompletedBookings(String userId, BookingSessionsCallback callback) {
        if (appContext != null && !NetworkUtils.isNetworkAvailable(appContext)) {
            loadCachedSessions(ENDPOINT_COMPLETED_BOOKINGS, userId, callback);
            return;
        }
        api.getUserCompletedBookings(userId).enqueue(createSessionCallback(callback, ENDPOINT_COMPLETED_BOOKINGS, userId));
    }

    public void updateReservation(String reservationId, ReservationUpdateRequest request, ReservationCallback callback) {
        if (appContext != null && !NetworkUtils.isNetworkAvailable(appContext)) {
            queuePendingRequest("PUT", ENDPOINT_RESERVATION + "/" + reservationId, request);
            callback.onError("No internet connection. Update stored locally and will sync once online.");
            return;
        }
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
                queuePendingRequest("PUT", ENDPOINT_RESERVATION + "/" + reservationId, request);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error. Update saved and will retry.");
            }
        });
    }

    public void cancelReservation(String reservationId, ReservationActionCallback callback) {
        if (appContext != null && !NetworkUtils.isNetworkAvailable(appContext)) {
            queuePendingRequest("PATCH", ENDPOINT_RESERVATION + "/" + reservationId + "/cancel", null);
            callback.onError("No internet connection. Cancellation saved locally and will sync once online.");
            return;
        }
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
                queuePendingRequest("PATCH", ENDPOINT_RESERVATION + "/" + reservationId + "/cancel", null);
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error. Cancellation saved and will retry.");
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

    private Callback<List<BookingSessionResponse>> createSessionCallback(BookingSessionsCallback callback,
                                                                         String endpoint,
                                                                         String requestKey) {
        return new Callback<List<BookingSessionResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<BookingSessionResponse>> call,
                                   @NonNull Response<List<BookingSessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookingSessionResponse> sessions = response.body();
                    callback.onSuccess(sessions);
                    cacheSessions(endpoint, requestKey, sessions);
                } else {
                    if (!loadCachedSessions(endpoint, requestKey, callback)) {
                        callback.onError("Failed to load bookings");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BookingSessionResponse>> call, @NonNull Throwable t) {
                if (!loadCachedSessions(endpoint, requestKey, callback)) {
                    callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
                }
            }
        };
    }

    private void cacheSessions(String endpoint, String key, List<BookingSessionResponse> sessions) {
        if (dbHelper == null) {
            return;
        }
        List<BookingSessionResponse> safeList = sessions != null ? sessions : new ArrayList<>();
        dbHelper.saveCachedResponse(endpoint, key, gson.toJson(safeList));
    }

    private boolean loadCachedSessions(String endpoint, String key, BookingSessionsCallback callback) {
        if (dbHelper == null) {
            return false;
        }
        String cached = dbHelper.getCachedResponse(endpoint, key);
        if (cached == null) {
            return false;
        }
        List<BookingSessionResponse> sessions = gson.fromJson(cached, sessionListType);
        callback.onSuccess(sessions != null ? sessions : new ArrayList<>());
        return true;
    }

    private void queuePendingRequest(String method, String endpoint, Object payload) {
        if (dbHelper == null) {
            return;
        }
        String body = payload != null ? gson.toJson(payload) : null;
        dbHelper.insertPendingRequest(method, endpoint, body);
        OfflineSyncManager manager = OfflineSyncManager.getInstance();
        if (manager != null) {
            manager.triggerSyncIfNeeded();
        }
    }
}
