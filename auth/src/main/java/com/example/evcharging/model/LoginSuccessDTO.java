package com.example.evcharging.model;

public class LoginSuccessDTO {
    private String success;
    private String message;
    private EVOwner evOwner;
    private String token;

    public LoginSuccessDTO(String success, String message, EVOwner evOwner, String token) {
        this.success = success;
        this.message = message;
        this.evOwner = evOwner;
        this.token = token;
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
}
