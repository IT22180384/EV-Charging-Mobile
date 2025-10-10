package com.example.evcharging.view.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.util.Log;

import com.example.evcharging.R;
import com.example.evcharging.databinding.ActivityProfileBinding;
import com.example.evcharging.view.base.BaseActivity;
import com.example.evcharging.model.EVOwner;
import com.example.evcharging.http.Api;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.utils.SpUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private ActivityProfileBinding binding;
    private Api apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeApi();
        loadProfileContent();
        showNavMenu(); // Show navigation for this activity
        setupClickListeners();
        loadUserData();
    }

    private void initializeApi() {
        apiService = RetrofitProvider.getInstance().create(Api.class);
    }

    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
        }
    }

    private void loadProfileContent() {
        // Load profile content into the base activity's content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            binding = ActivityProfileBinding.inflate(getLayoutInflater());
            contentContainer.addView(binding.getRoot());
        }
    }

    private void setupClickListeners() {
        if (binding == null) return;

        // Edit avatar
        binding.btnEditAvatar.setOnClickListener(v -> {
            Toast.makeText(this, "Edit avatar functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Edit profile
        binding.btnEditProfile.setOnClickListener(v -> {
            // For testing purposes - test API call with hardcoded NIC
            String testNic = "987654321V";
            Log.d(TAG, "Testing API with NIC: " + testNic);
            testApiCall(testNic);
        });

        // Profile info items
        binding.itemFullName.setOnClickListener(v -> {
            Toast.makeText(this, "Edit full name", Toast.LENGTH_SHORT).show();
        });

        binding.itemEmail.setOnClickListener(v -> {
            Toast.makeText(this, "Email cannot be changed", Toast.LENGTH_SHORT).show();
        });

        binding.itemPhone.setOnClickListener(v -> {
            Toast.makeText(this, "Edit phone number", Toast.LENGTH_SHORT).show();
        });

        binding.itemNic.setOnClickListener(v -> {
            Toast.makeText(this, "NIC cannot be changed", Toast.LENGTH_SHORT).show();
        });

        binding.itemVehicleType.setOnClickListener(v -> {
            Toast.makeText(this, "Edit vehicle type", Toast.LENGTH_SHORT).show();
        });

        // Change password
        binding.itemChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.change_password_coming_soon), Toast.LENGTH_SHORT).show();
        });

        // Logout
        binding.itemLogout.setOnClickListener(v -> {
            showLogoutDialog();
        });

        // Delete account
        binding.itemDeleteAccount.setOnClickListener(v -> {
            showDeleteAccountDialog();
        });
    }

    private void loadUserData() {
        if (binding == null) return;

        // Get user NIC from shared preferences
        String userNic = SpUtil.getNic();
        if (userNic == null || userNic.isEmpty()) {
            Log.e(TAG, "User NIC not found in preferences");
            showErrorMessage("User information not found. Please login again.");
            return;
        }

        // Show loading state
        showLoadingState(true);

        // Fetch user profile data from API
        Call<EVOwner> call = apiService.getEVOwnerProfile(userNic);
        call.enqueue(new Callback<EVOwner>() {
            @Override
            public void onResponse(Call<EVOwner> call, Response<EVOwner> response) {
                showLoadingState(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    EVOwner evOwner = response.body();
                    updateUIWithUserData(evOwner);
                } else {
                    Log.e(TAG, "Failed to fetch profile data: " + response.code());
                    showErrorMessage("Failed to load profile data. Error: " + response.code());
                    loadFallbackUserData();
                }
            }

            @Override
            public void onFailure(Call<EVOwner> call, Throwable t) {
                showLoadingState(false);
                Log.e(TAG, "Network error while fetching profile data", t);
                showErrorMessage("Network error: " + t.getMessage());
                loadFallbackUserData();
            }
        });
    }

    private void updateUIWithUserData(EVOwner evOwner) {
        if (binding == null || evOwner == null) return;

        // Update profile header
        binding.textUserName.setText(evOwner.getName() != null ? evOwner.getName() : "N/A");
        binding.textUserEmail.setText(evOwner.getEmail() != null ? evOwner.getEmail() : "N/A");
        
        // Update profile details
        binding.textFullName.setText(evOwner.getName() != null ? evOwner.getName() : "N/A");
        binding.textEmail.setText(evOwner.getEmail() != null ? evOwner.getEmail() : "N/A");
        binding.textPhone.setText(evOwner.getPhone() != null ? evOwner.getPhone() : "N/A");
        binding.textNic.setText(evOwner.getNic() != null ? evOwner.getNic() : "N/A");
        binding.textVehicleType.setText(evOwner.getVehicleType() != null ? evOwner.getVehicleType() : "N/A");
        
        // Set joined date from stored data if available, or show placeholder
        String joinedDate = "Account created";
        binding.textJoinedDate.setText(joinedDate);

        Log.d(TAG, "Profile data loaded successfully for user: " + evOwner.getName());
    }

    private void loadFallbackUserData() {
        if (binding == null) return;

        // Load fallback data from shared preferences
        String userName = SpUtil.getUserName();
        String userEmail = SpUtil.getUserEmail();
        
        binding.textUserName.setText(userName != null ? userName : "User");
        binding.textUserEmail.setText(userEmail != null ? userEmail : "N/A");
        binding.textFullName.setText(userName != null ? userName : "User");
        binding.textEmail.setText(userEmail != null ? userEmail : "N/A");
        binding.textPhone.setText("N/A");
        binding.textNic.setText(SpUtil.getNic() != null ? SpUtil.getNic() : "N/A");
        binding.textVehicleType.setText("N/A");
        binding.textJoinedDate.setText("N/A");
    }

    private void showLoadingState(boolean isLoading) {
        if (binding == null) return;
        
        // You can add a progress bar to your layout and show/hide it here
        // For now, we'll just disable the UI elements during loading
        binding.getRoot().setAlpha(isLoading ? 0.5f : 1.0f);
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Test method for API call - can be removed in production
    private void testApiCall(String testNic) {
        Call<EVOwner> call = apiService.getEVOwnerProfile(testNic);
        
        Toast.makeText(this, "Testing API call for NIC: " + testNic, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Testing API call for NIC: " + testNic);

        call.enqueue(new Callback<EVOwner>() {
            @Override
            public void onResponse(Call<EVOwner> call, Response<EVOwner> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EVOwner evOwner = response.body();
                    Log.d(TAG, "API Test Success - Name: " + evOwner.getName() + 
                                ", Email: " + evOwner.getEmail() + 
                                ", Phone: " + evOwner.getPhone() +
                                ", Vehicle Type: " + evOwner.getVehicleType());
                    Toast.makeText(ProfileActivity.this, "API Test Success: " + evOwner.getName(), Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "API Test Failed - Response Code: " + response.code());
                    Toast.makeText(ProfileActivity.this, "API Test Failed - Code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<EVOwner> call, Throwable t) {
                Log.e(TAG, "API Test Network Error", t);
                Toast.makeText(ProfileActivity.this, "API Test Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.logout), (dialog, which) -> {
                    // Handle logout logic here
                    Toast.makeText(this, "Logout functionality coming soon", Toast.LENGTH_SHORT).show();
                    // In a real app, you would clear user session and navigate to login
                })
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_account_title))
                .setMessage(getString(R.string.delete_account_message))
                .setPositiveButton(getString(R.string.btn_delete), (dialog, which) -> {
                    Toast.makeText(this, "Delete account functionality coming soon", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }
}