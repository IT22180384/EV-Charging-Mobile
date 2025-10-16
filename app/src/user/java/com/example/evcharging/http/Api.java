package com.example.evcharging.http;

import com.example.evcharging.model.Booking;
import com.example.evcharging.model.ChargingStation;
import com.example.evcharging.model.EVOwner;
import com.example.evcharging.http.ApiResponse;
import com.example.evcharging.model.LoginSuccessDTO;
import com.example.evcharging.http.dto.BookingSessionResponse;
import com.example.evcharging.http.dto.ReservationCreateRequest;
import com.example.evcharging.http.dto.ReservationUpdateRequest;
import com.example.evcharging.http.dto.ReservationResponse;
import com.example.evcharging.http.dto.StationDetailResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    // Auth APIs must return concrete DTOs for Gson to deserialize correctly
    @POST("/api/auth/login")
    Call<LoginSuccessDTO> operatorLogin(@Body RequestBody body);

    @POST("/api/evowners/login")
    Call<LoginSuccessDTO> evOwnerLogin(@Body RequestBody body);

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
    @POST("/api/reservation")
    Call<ReservationResponse> createReservation(@Body ReservationCreateRequest request);

    @GET("/api/reservation/{id}")
    Call<ReservationResponse> getReservation(@Path("id") String id);

   @GET("/api/reservation/history/{nic}")
    Call<List<ReservationResponse>> getReservationHistory(@Path("nic") String nic);

    @PUT("/api/reservation/{id}")
    Call<ReservationResponse> updateReservation(@Path("id") String id, @Body ReservationUpdateRequest request);

    @PATCH("/api/reservation/cancel/{id}")
    Call<Void> cancelReservation(@Path("id") String id);

    @GET("/api/reservation/user/{userId}/bookings/pending")
    Call<java.util.List<BookingSessionResponse>> getUserPendingBookings(@Path("userId") String userId);

    @GET("/api/reservation/user/{userId}/bookings/completed")
    Call<java.util.List<BookingSessionResponse>> getUserCompletedBookings(@Path("userId") String userId);

    @GET("/api/stations/{id}")
    Call<ApiResponse<StationDetailResponse>> getStationDetail(@Path("id") String stationId);

    @POST("/api/bookings/{bookingId}/cancel")
    Call<ApiResponse<Void>> cancelBooking(@Path("bookingId") String bookingId);

    @POST("/api/bookings/{bookingId}/modify")
    Call<ApiResponse<Booking>> modifyBooking(@Path("bookingId") String bookingId, @Body RequestBody bookingData);

    // EVOwner profile API
    @GET("/api/evowners/{nic}")
    Call<EVOwner> getEVOwnerProfile(@Path("nic") String nic);

    @PUT("/api/evowners/{nic}")
    Call<EVOwner> updateEVOwnerProfile(@Path("nic") String nic, @Body RequestBody profileData);

    @PATCH("/api/evowners/{nic}/deactivate")
    Call<Void> deactivateEVOwnerAccount(@Path("nic") String nic);
}
