package com.example.evcharging.http;

import com.example.evcharging.model.LoginSuccessDTO;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("/api/evowners/login")
    Call<LoginSuccessDTO> login(@Body RequestBody body);

    @POST("/api/evowners/register")
    Call<LoginSuccessDTO> register(@Body RequestBody body);
}

