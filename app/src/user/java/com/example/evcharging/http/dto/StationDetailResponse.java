package com.example.evcharging.http.dto;

import com.google.gson.annotations.SerializedName;

public class StationDetailResponse {
    @SerializedName(value = "id", alternate = {"Id"})
    public String id;

    @SerializedName(value = "name", alternate = {"Name"})
    public String name;

    @SerializedName(value = "address", alternate = {"Address"})
    public String address;

    @SerializedName(value = "location", alternate = {"Location"})
    public StationLocationResponse location;

    public static class StationLocationResponse {
        @SerializedName(value = "address", alternate = {"Address"})
        public String address;

        @SerializedName(value = "latitude", alternate = {"Latitude"})
        public Double latitude;

        @SerializedName(value = "longitude", alternate = {"Longitude"})
        public Double longitude;
    }
}
