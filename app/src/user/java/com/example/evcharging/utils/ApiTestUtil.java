package com.example.evcharging.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.evcharging.http.Api;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.model.EVOwner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Utility class for testing API calls
 */
public class ApiTestUtil {
    private static final String TAG = "ApiTestUtil";

    /**
     * Test method to fetch EV Owner profile by NIC
     * This is for testing purposes and can be removed in production
     */
    public static void testFetchEVOwnerProfile(Context context, String nic) {
        if (nic == null || nic.trim().isEmpty()) {
            Toast.makeText(context, "NIC is required for testing", Toast.LENGTH_SHORT).show();
            return;
        }

        Api apiService = RetrofitProvider.getInstance().create(Api.class);
        Call<EVOwner> call = apiService.getEVOwnerProfile(nic);

        Log.d(TAG, "Testing API call for NIC: " + nic);
        Toast.makeText(context, "Testing API call for NIC: " + nic, Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<EVOwner>() {
            @Override
            public void onResponse(Call<EVOwner> call, Response<EVOwner> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EVOwner evOwner = response.body();
                    Log.d(TAG, "API Test Success - Name: " + evOwner.getName() + 
                                ", Email: " + evOwner.getEmail() + 
                                ", Phone: " + evOwner.getPhone() +
                                ", Vehicle Type: " + evOwner.getVehicleType());
                    Toast.makeText(context, "API Test Success: " + evOwner.getName(), Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "API Test Failed - Response Code: " + response.code());
                    Toast.makeText(context, "API Test Failed - Code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<EVOwner> call, Throwable t) {
                Log.e(TAG, "API Test Network Error", t);
                Toast.makeText(context, "API Test Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}