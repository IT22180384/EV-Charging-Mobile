package com.example.evcharging.http;

import com.example.evcharging.model.Booking;
import com.example.evcharging.model.ChargingStation;
import com.example.evcharging.http.ApiResponse;
import com.example.evcharging.model.LoginSuccessDTO;
import com.example.evcharging.http.dto.ReservationCreateRequest;
import com.example.evcharging.http.dto.ReservationResponse;

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

    // Reservation APIs (backend: ReservationController)
    @POST("/api/Reservation")
    Call<ReservationResponse> createReservation(@Body ReservationCreateRequest request);

    @GET("/api/Reservation/{id}")
    Call<ReservationResponse> getReservation(@Path("id") String id);

    @GET("/api/Reservation/history/{nic}")
    Call<java.util.List<ReservationResponse>> getReservationHistory(@Path("nic") String nic);

    @POST("/api/bookings/{bookingId}/cancel")
    Call<ApiResponse<Void>> cancelBooking(@Path("bookingId") String bookingId);

    @POST("/api/bookings/{bookingId}/modify")
    Call<ApiResponse<Booking>> modifyBooking(@Path("bookingId") String bookingId, @Body RequestBody bookingData);
}
