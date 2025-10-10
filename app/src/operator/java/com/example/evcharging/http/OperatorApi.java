package com.example.evcharging.http;

import com.example.evcharging.model.LoginSuccessDTO;
import com.example.evcharging.http.ApiResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OperatorApi {
    // Auth APIs for operators
    @POST("/api/auth/login")
    Call<LoginSuccessDTO> operatorLogin(@Body RequestBody body);

    @POST("/api/evowners/login")
    Call<LoginSuccessDTO> evOwnerLogin(@Body RequestBody body);

    // Operator-specific APIs
    @GET("/api/operators/sessions/assigned")
    Call<ApiResponse<List<Object>>> getAssignedSessions(@Query("status") String status);

    @PATCH("/api/operators/session/{bookingId}/complete")
    Call<ApiResponse<Object>> completeSession(@Path("bookingId") String bookingId, @Body RequestBody finalizeData);

    @GET("/api/operators/available")
    Call<ApiResponse<Object>> getAvailableOperator(@Query("stationId") String stationId, @Query("reservationDateTime") String dateTime);
}