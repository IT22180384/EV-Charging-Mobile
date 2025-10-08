package com.example.evcharging.http.dto;

public class ReservationCreateRequest {
    public String userId;
    public String chargingStationId;
    public String startTime; // ISO 8601 (UTC)
    public String endTime;   // ISO 8601 (UTC)
    public String notes;

    public ReservationCreateRequest(String userId, String chargingStationId, String startTime, String endTime, String notes) {
        this.userId = userId;
        this.chargingStationId = chargingStationId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
    }
}

