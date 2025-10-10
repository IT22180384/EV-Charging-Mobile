package com.example.evcharging.model;

public class EVOwner {
    private String id;
    private String name;
    private String email;
    private String nic;
    private Boolean isActive;
    private String vehicleType;
    private String phone;

    // Default constructor for JSON parsing
    public EVOwner() {}

    public EVOwner(String id, String name, String email, String nic, Boolean isActive, String vehicleType, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nic = nic;
        this.isActive = isActive;
        this.vehicleType = vehicleType;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}