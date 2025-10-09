package com.example.evcharging.http;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.evcharging.utils.SpUtil;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public abstract class HttpCallback<T> implements Callback<T> {
    private static final String TAG = "HttpCallback";
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int SERVICE_UNAVAILABLE = 503;

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            onResult(response.body());
        } else {
            // Handle 401 unauthorized - logout user immediately
            if (response.code() == UNAUTHORIZED) {
                handleUnauthorized();
            }
            handleErrorResponse(response.code(), response.errorBody());
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        Log.e(TAG, "Network request failed", t);
        onError(handleException(t).message);
    }

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, httpException.code());
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    ex.message = "Session expired. Please login again.";
                    break;
                case FORBIDDEN:
                    ex.message = "Access forbidden";
                    break;
                case NOT_FOUND:
                    ex.message = "Resource not found";
                    break;
                case REQUEST_TIMEOUT:
                    ex.message = "Request timed out";
                    break;
                case INTERNAL_SERVER_ERROR:
                    ex.message = "Internal server error";
                    break;
                case SERVICE_UNAVAILABLE:
                    ex.message = "Service unavailable";
                    break;
                default:
                    ex.message = "HTTP error: " + httpException.code();
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException || e instanceof JSONException
                || e instanceof MalformedJsonException) {
            ex = new ResponseThrowable(e, 1001);
            ex.message = "Parsing error";
        } else if (e instanceof ConnectException) {
            ex = new ResponseThrowable(e, 1002);
            ex.message = "Network connection error";
        } else if (e instanceof SSLHandshakeException) {
            ex = new ResponseThrowable(e, 1003);
            ex.message = "SSL handshake failed";
        } else if (e instanceof SocketTimeoutException) {
            ex = new ResponseThrowable(e, 1004);
            ex.message = "Connection timed out";
        } else if (e instanceof java.io.EOFException) {
            ex = new ResponseThrowable(e, 1005);
            ex.message = "Empty response body";
        } else {
            ex = new ResponseThrowable(e, 1000);
            ex.message = "Unknown error";
        }
        return ex;
    }

    @Nullable
    public abstract void onResult(T result);

    public abstract void onError(@NonNull String errorMessage);

    private void handleUnauthorized() {
        // Clear all authentication data
        SpUtil.logout();
        // Notify about unauthorized access
        onUnauthorized();
    }

    /**
     * Called when a 401 unauthorized response is received.
     * Override this method to handle logout logic (e.g., redirect to login screen)
     */
    protected void onUnauthorized() {
        // Default implementation - can be overridden by subclasses
        onError("Session expired. Please login again.");
    }

    private void handleErrorResponse(int code, @Nullable ResponseBody body) {
        try {
            if (body != null) {
                String errorJson = body.string();
                Log.e(TAG, "Error response: " + errorJson);
                onError("HTTP Error " + code);
            } else {
                onError("HTTP Error " + code);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading response body", e);
            onError("Unexpected error while handling response");
        }
    }
}