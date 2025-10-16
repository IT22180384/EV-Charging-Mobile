package com.example.evcharging.data.sync;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.evcharging.data.local.DBHelper;
import com.example.evcharging.http.Api;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.http.dto.ReservationCreateRequest;
import com.example.evcharging.http.dto.ReservationResponse;
import com.example.evcharging.http.dto.ReservationUpdateRequest;
import com.example.evcharging.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class OfflineSyncManager {

    private static final String TAG = "OfflineSyncManager";
    private static final String RESERVATION_ENDPOINT = "/api/reservation";

    private static volatile OfflineSyncManager instance;

    private final Context appContext;
    private final DBHelper dbHelper;
    private final Api api;
    private final Gson gson;
    private final ExecutorService executor;
    private final Object syncLock = new Object();
    private boolean isSyncing;

    public static void init(Context context) {
        if (instance == null) {
            synchronized (OfflineSyncManager.class) {
                if (instance == null) {
                    instance = new OfflineSyncManager(context.getApplicationContext());
                }
            }
        }
    }

    public static OfflineSyncManager getInstance() {
        return instance;
    }

    private OfflineSyncManager(Context context) {
        this.appContext = context;
        this.dbHelper = DBHelper.getInstance(context);
        this.api = RetrofitProvider.getInstance().create(Api.class);
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadExecutor();
        registerNetworkCallback();
        triggerSyncIfNeeded();
    }

    private void registerNetworkCallback() {
        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return;
        }

        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                triggerSyncIfNeeded();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(callback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            cm.registerNetworkCallback(request, callback);
        }
    }

    public void triggerSyncIfNeeded() {
        if (!NetworkUtils.isNetworkAvailable(appContext)) {
            return;
        }
        synchronized (syncLock) {
            if (isSyncing) {
                return;
            }
            isSyncing = true;
        }
        executor.execute(() -> {
            try {
                syncPendingRequests();
            } finally {
                synchronized (syncLock) {
                    isSyncing = false;
                }
            }
        });
    }

    private void syncPendingRequests() {
        List<DBHelper.PendingRequest> requests = dbHelper.getPendingRequests();
        if (requests.isEmpty()) {
            return;
        }
        for (DBHelper.PendingRequest request : requests) {
            boolean success = processRequest(request);
            if (success) {
                dbHelper.deletePendingRequest(request.id);
            } else {
                // Stop processing to avoid hammering the backend with a failing payload.
                break;
            }
        }
    }

    private boolean processRequest(DBHelper.PendingRequest request) {
        try {
            switch (request.method) {
                case "POST":
                    if (RESERVATION_ENDPOINT.equals(request.endpoint)) {
                        ReservationCreateRequest body = gson.fromJson(request.body, ReservationCreateRequest.class);
                        Response<ReservationResponse> response = api.createReservation(body).execute();
                        return response.isSuccessful();
                    }
                    break;
                case "PUT":
                    if (request.endpoint.startsWith(RESERVATION_ENDPOINT + "/")) {
                        String id = request.endpoint.substring((RESERVATION_ENDPOINT + "/").length());
                        ReservationUpdateRequest updateBody = gson.fromJson(request.body, ReservationUpdateRequest.class);
                        Response<ReservationResponse> response = api.updateReservation(id, updateBody).execute();
                        return response.isSuccessful();
                    }
                    break;
                case "PATCH":
                    if (request.endpoint.startsWith(RESERVATION_ENDPOINT + "/") && request.endpoint.endsWith("/cancel")) {
                        String id = request.endpoint.substring((RESERVATION_ENDPOINT + "/").length(),
                                request.endpoint.length() - "/cancel".length());
                        Response<Void> response = api.cancelReservation(id).execute();
                        return response.isSuccessful();
                    }
                    break;
                default:
                    Log.w(TAG, "Unsupported pending request method: " + request.method);
                    break;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to process pending request: " + request.endpoint, ex);
            return false;
        }
        return false;
    }
}

