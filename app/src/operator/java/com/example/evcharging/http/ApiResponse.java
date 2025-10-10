package com.example.evcharging.http;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName(value = "success", alternate = {"Success"})
    private boolean success;

    @SerializedName(value = "data", alternate = {"Data"})
    private T data;

    @SerializedName(value = "message", alternate = {"Message"})
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}