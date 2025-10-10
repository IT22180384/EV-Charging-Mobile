package com.example.evcharging.model;

public class Operator {
    private String id;
    private String userId;
    private String stationId;
    private String name;
    private String email;
    private String phone;
    private Boolean isActive;

    public Operator(String id, String userId, String stationId, String name, String email, String phone, Boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.stationId = stationId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}