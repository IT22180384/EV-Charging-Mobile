package com.example.evcharging.http.dto;

import com.google.gson.annotations.SerializedName;

public class BookingSessionResponse {
    @SerializedName(value = "bookingId", alternate = {"BookingId"})
    public String bookingId;

    @SerializedName(value = "stationId", alternate = {"StationId", "chargingStationId"})
    public String stationId;

    @SerializedName(value = "stationName", alternate = {"StationName"})
    public String stationName;

    @SerializedName(value = "stationAddress", alternate = {"StationAddress"})
    public String stationAddress;

    @SerializedName(value = "id")
    public String id;

    @SerializedName(value = "reservationId", alternate = {"ReservationId"})
    public String reservationId;

    @SerializedName(value = "userId", alternate = {"UserId"})
    public String userId;

    @SerializedName(value = "status", alternate = {"Status"})
    public String status;

    @SerializedName(value = "reservationDateTime", alternate = {"ReservationDateTime"})
    public String reservationDateTime;

    @SerializedName(value = "startTime", alternate = {"StartTime"})
    public String startTime;

    @SerializedName(value = "endTime", alternate = {"EndTime"})
    public String endTime;

    @SerializedName(value = "checkInTime", alternate = {"CheckInTime"})
    public String checkInTime;

    @SerializedName(value = "checkOutTime", alternate = {"CheckOutTime"})
    public String checkOutTime;

    @SerializedName(value = "energyConsumedKWh", alternate = {"EnergyConsumedKWh"})
    public Double energyConsumedKWh;

    @SerializedName(value = "sessionDurationMinutes", alternate = {"SessionDurationMinutes"})
    public Integer sessionDurationMinutes;

    @SerializedName(value = "sessionNotes", alternate = {"SessionNotes"})
    public String sessionNotes;
}
