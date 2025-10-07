package com.example.evcharging.http;

public interface Api {
    @POST("/user/login")
    Call<LoginSuccessDTO> login(@Body RequestBody body);
}