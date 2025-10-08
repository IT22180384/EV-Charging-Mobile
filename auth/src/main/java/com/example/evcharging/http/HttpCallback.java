package com.example.evcharging.http;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public abstract class HttpCallback<T> implements Callback<T> {
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int SERVICE_UNAVAILABLE = 503;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onResult(response.body());
        } else {
            handleErrorResponse(response.code(), response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
        onError(handleException(t).message);
    }

    public static ResponseThrowable handleException(Throwable e) {
        ResponseThrowable ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponseThrowable(e, httpException.code());
            switch (httpException.code()) {
                case UNAUTHORIZED:
                    ex.message = "Unauthorized request";
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
                || e instanceof ParseException || e instanceof MalformedJsonException) {
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

    public abstract void onResult(T result);

    public abstract void onError(String errorMessage);
    
    private void handleErrorResponse(int code, ResponseBody body) {
        try {
            if (body != null) {
                String errorJson = body.string();
                System.out.println("Error response: " + errorJson);

                onError("HTTP Error " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError("Unexpected error while handling response");
        }
    }
}
