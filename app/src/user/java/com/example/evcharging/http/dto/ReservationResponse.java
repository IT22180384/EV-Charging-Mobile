package com.example.evcharging.http.dto;

import com.google.gson.annotations.SerializedName;

public class ReservationResponse {
    @SerializedName(value = "id", alternate = {"Id"})
    public String id;

    @SerializedName(value = "userId", alternate = {"UserId"})
    public String userId;

    @SerializedName(value = "chargingStationId", alternate = {"ChargingStationId"})
    public String chargingStationId;

    @SerializedName(value = "startTime", alternate = {"StartTime"})
    public String startTime;

    @SerializedName(value = "endTime", alternate = {"EndTime"})
    public String endTime;

    @SerializedName(value = "status", alternate = {"Status"})
    public String status;

    @SerializedName(value = "qrCode", alternate = {"QrCode", "QRCode"})
    public String qrCode;

    @SerializedName(value = "operatorId", alternate = {"OperatorId"})
    public String operatorId;

    @SerializedName(value = "bookingId", alternate = {"BookingId"})
    public String bookingId;

    @SerializedName(value = "createdAt", alternate = {"CreatedAt"})
    public String createdAt;

    @SerializedName(value = "updatedAt", alternate = {"UpdatedAt"})
    public String updatedAt;

    @SerializedName(value = "notes", alternate = {"Notes"})
    public String notes;
}
