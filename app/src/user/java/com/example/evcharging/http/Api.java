package com.example.evcharging.http;

import com.example.evcharging.model.Booking;
import com.example.evcharging.model.ChargingStation;
import com.example.evcharging.http.ApiResponse;
import com.example.evcharging.model.LoginSuccessDTO;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    // Auth APIs must return concrete DTOs for Gson to deserialize correctly
    @POST("/api/evowners/login")
    Call<LoginSuccessDTO> login(@Body RequestBody body);

    @POST("/api/evowners/register")
    Call<LoginSuccessDTO> register(@Body RequestBody body);

    // User-specific APIs for EV charging
    @GET("/api/stations")
    Call<ApiResponse<List<ChargingStation>>> getChargingStations(@Query("active") Boolean active);

    @GET("/api/stations/nearby")
    Call<ApiResponse<List<ChargingStation>>> getNearbyStations(@Query("lat") double latitude, @Query("lng") double longitude, @Query("radiusKm") Double radiusKm);

    @GET("/api/stations/active")
    Call<ApiResponse<List<ChargingStation>>> getActiveStations();

    @GET("/api/bookings/user/{userId}")
    Call<ApiResponse<List<Booking>>> getUserBookings(@Path("userId") String userId);

    @POST("/api/bookings")
    Call<ApiResponse<Booking>> createBooking(@Body RequestBody bookingData);

    @POST("/api/bookings/{bookingId}/cancel")
    Call<ApiResponse<Void>> cancelBooking(@Path("bookingId") String bookingId);

    @POST("/api/bookings/{bookingId}/modify")
    Call<ApiResponse<Booking>> modifyBooking(@Path("bookingId") String bookingId, @Body RequestBody bookingData);
}
