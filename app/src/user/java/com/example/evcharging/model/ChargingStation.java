package com.example.evcharging.model;

import com.google.gson.annotations.SerializedName;

public class ChargingStation {
    @SerializedName(value = "id", alternate = {"Id"})
    private String id;

    @SerializedName(value = "name", alternate = {"Name"})
    private String name;

    // Not provided by backend DTO; kept for UI compatibility if needed
    private String location;

    @SerializedName(value = "address", alternate = {"Address"})
    private String address;

    @SerializedName(value = "latitude", alternate = {"Latitude"})
    private double latitude;

    @SerializedName(value = "longitude", alternate = {"Longitude"})
    private double longitude;

    @SerializedName(value = "availableSlots", alternate = {"AvailableSlots"})
    private int availableSlots;

    @SerializedName(value = "totalSlots", alternate = {"TotalSlots"})
    private int totalSlots;

    // Optional/placeholder fields for UI; backend does not send these
    private double pricePerHour;

    // Backend uses "Type" (AC/DC); keeping separate field name for clarity
    @SerializedName(value = "type", alternate = {"Type"})
    private String stationType;

    @SerializedName(value = "isActive", alternate = {"IsActive"})
    private boolean isActive;

    public ChargingStation() {
    }

    public ChargingStation(String id, String name, String location, String address,
                           double latitude, double longitude, int availableSlots,
                           int totalSlots, double pricePerHour, String stationType, boolean isActive) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availableSlots = availableSlots;
        this.totalSlots = totalSlots;
        this.pricePerHour = pricePerHour;
        this.stationType = stationType;
        this.isActive = isActive;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(int availableSlots) { this.availableSlots = availableSlots; }

    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }

    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }

    public String getStationType() { return stationType; }
    public void setStationType(String stationType) { this.stationType = stationType; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
