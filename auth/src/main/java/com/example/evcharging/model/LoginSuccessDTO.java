package com.example.evcharging.model;

public class LoginSuccessDTO {
    private String success;
    private String message;
    private EVOwner evOwner;
    private Operator operator;
    private String token;
    private String userType; // "EVOwner" or "StationOperator"

    public LoginSuccessDTO(String success, String message, EVOwner evOwner, String token) {
        this.success = success;
        this.message = message;
        this.evOwner = evOwner;
        this.token = token;
        this.userType = "EVOwner";
    }

    public LoginSuccessDTO(String success, String message, Operator operator, String token) {
        this.success = success;
        this.message = message;
        this.operator = operator;
        this.token = token;
        this.userType = "StationOperator";
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EVOwner getEvOwner() {
        return evOwner;
    }

    public void setEvOwner(EVOwner evOwner) {
        this.evOwner = evOwner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
        this.userType = "StationOperator";
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
