package com.example.evcharging.user.http;

import com.example.evcharging.http.ApiResponse;
import com.example.evcharging.model.Booking;
import com.example.evcharging.model.ChargingStation;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    // User-specific APIs for EV charging ONLY
    @GET("/api/stations")
    Call<ApiResponse<List<ChargingStation>>> getChargingStations(@Query("active") Boolean active);

    @GET("/api/stations/active")
    Call<ApiResponse<List<ChargingStation>>> getActiveStations();

    @GET("/api/stations/nearby")
    Call<ApiResponse<List<ChargingStation>>> getNearbyStations(@Query("lat") double latitude,
                                                               @Query("lng") double longitude,
                                                               @Query("radius") Double radiusKm);

    @GET("/api/bookings/user/{userId}")
    Call<ApiResponse<List<Booking>>> getUserBookings(@Path("userId") String userId);

    @POST("/api/bookings")
    Call<ApiResponse<Booking>> createBooking(@Body RequestBody bookingData);

    @POST("/api/bookings/{bookingId}/cancel")
    Call<ApiResponse<Void>> cancelBooking(@Path("bookingId") String bookingId);

    @POST("/api/bookings/{bookingId}/modify")
    Call<ApiResponse<Booking>> modifyBooking(@Path("bookingId") String bookingId, @Body RequestBody bookingData);
}
