package com.example.evcharging.http.dto;

import com.google.gson.annotations.SerializedName;

public class ReservationUpdateRequest {
    @SerializedName(value = "startTime", alternate = {"StartTime"})
    public String startTime;

    @SerializedName(value = "endTime", alternate = {"EndTime"})
    public String endTime;

    @SerializedName(value = "status", alternate = {"Status"})
    public String status;

    @SerializedName(value = "notes", alternate = {"Notes"})
    public String notes;

    public ReservationUpdateRequest(String startTime, String endTime, String status, String notes) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.notes = notes;
    }
}
