package com.example.evcharging.view.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.util.Log;

import com.example.evcharging.R;
import com.example.evcharging.databinding.ActivityProfileBinding;
import com.example.evcharging.databinding.DialogEditProfileBinding;
import com.example.evcharging.databinding.DialogDeactivateAccountBinding;
import com.example.evcharging.view.base.BaseActivity;
import com.example.evcharging.model.EVOwner;
import com.example.evcharging.http.Api;
import com.example.evcharging.http.RetrofitProvider;
import com.example.evcharging.utils.SpUtil;
import com.google.gson.Gson;
import android.content.Intent;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private ActivityProfileBinding binding;
    private Api apiService;
    private EVOwner currentUser;

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
            showEditProfileDialog();
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

        // Deactivate account
        binding.itemDeactivateAccount.setOnClickListener(v -> {
            showDeactivateAccountDialog();
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
                    currentUser = response.body();
                    updateUIWithUserData(currentUser);
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

    private void showDeactivateAccountDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        DialogDeactivateAccountBinding dialogBinding = DialogDeactivateAccountBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(dialogBinding.getRoot());
        
        // Set window properties for Apple aesthetic
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set up click listeners
        dialogBinding.btnCancelDeactivate.setOnClickListener(v -> dialog.dismiss());
        
        dialogBinding.btnConfirmDeactivate.setOnClickListener(v -> {
            deactivateAccount(dialogBinding, dialog);
        });

        dialog.show();
    }

    private void deactivateAccount(DialogDeactivateAccountBinding dialogBinding, Dialog dialog) {
        // Show loading state
        dialogBinding.buttonsContainer.setVisibility(View.GONE);
        dialogBinding.loadingContainer.setVisibility(View.VISIBLE);

        // Get user NIC
        String userNic = SpUtil.getNic();
        if (userNic == null || userNic.isEmpty()) {
            Log.e(TAG, "User NIC not found for deactivation");
            showErrorMessage("Unable to deactivate account. Please try again.");
            dialog.dismiss();
            return;
        }

        // Make API call to deactivate account
        Call<Void> call = apiService.deactivateEVOwnerAccount(userNic);
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Account deactivated successfully");
                    
                    // Clear user session data
                    SpUtil.logout();
                    
                    // Show success message
                    Toast.makeText(ProfileActivity.this, "Account deactivated successfully", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to login screen
                    navigateToLogin();
                    
                    dialog.dismiss();
                } else {
                    Log.e(TAG, "Failed to deactivate account: " + response.code());
                    dialogBinding.buttonsContainer.setVisibility(View.VISIBLE);
                    dialogBinding.loadingContainer.setVisibility(View.GONE);
                    showErrorMessage("Failed to deactivate account. Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network error while deactivating account", t);
                dialogBinding.buttonsContainer.setVisibility(View.VISIBLE);
                dialogBinding.loadingContainer.setVisibility(View.GONE);
                showErrorMessage("Network error: " + t.getMessage());
            }
        });
    }

    private void navigateToLogin() {
        try {
            // Try to navigate to login activity
            Class<?> loginClass = Class.forName("com.example.evcharging.view.auth.LoginActivity");
            Intent intent = new Intent(this, loginClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Login activity not found", e);
            // Alternative: close the app or navigate to a known activity
            finishAffinity(); // Close all activities and exit app
        }
    }

    private void showEditProfileDialog() {
        if (currentUser == null) {
            Toast.makeText(this, "Please wait for profile data to load", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        DialogEditProfileBinding dialogBinding = DialogEditProfileBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(dialogBinding.getRoot());
        
        // Set window properties for Apple aesthetic
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Pre-populate fields with current user data
        dialogBinding.editFullName.setText(currentUser.getName());
        dialogBinding.editEmail.setText(currentUser.getEmail());
        dialogBinding.editPhone.setText(currentUser.getPhone());
        dialogBinding.editNic.setText(currentUser.getNic());
        dialogBinding.editVehicleType.setText(currentUser.getVehicleType());

        // Set up click listeners
        dialogBinding.btnCloseEdit.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnCancelEdit.setOnClickListener(v -> dialog.dismiss());
        
        dialogBinding.btnSaveProfile.setOnClickListener(v -> {
            updateUserProfile(dialogBinding, dialog);
        });

        dialog.show();
    }

    private void updateUserProfile(DialogEditProfileBinding dialogBinding, Dialog dialog) {
        String updatedName = dialogBinding.editFullName.getText().toString().trim();
        String updatedPhone = dialogBinding.editPhone.getText().toString().trim();
        String updatedVehicleType = dialogBinding.editVehicleType.getText().toString().trim();

        if (updatedName.isEmpty()) {
            dialogBinding.editFullName.setError("Name is required");
            return;
        }

        if (updatedPhone.isEmpty()) {
            dialogBinding.editPhone.setError("Phone is required");
            return;
        }

        // Create updated EVOwner object
        EVOwner updatedUser = new EVOwner(
            currentUser.getId(),
            updatedName,
            currentUser.getEmail(), // Email cannot be changed
            currentUser.getNic(), // NIC cannot be changed
            currentUser.getIsActive(),
            updatedVehicleType,
            updatedPhone
        );

        // Convert to JSON
        Gson gson = new Gson();
        String json = gson.toJson(updatedUser);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        // Show loading
        dialogBinding.btnSaveProfile.setEnabled(false);
        dialogBinding.btnSaveProfile.setText("Saving...");

        // Make API call
        String userNic = SpUtil.getNic();
        Call<EVOwner> call = apiService.updateEVOwnerProfile(userNic, requestBody);
        
        call.enqueue(new Callback<EVOwner>() {
            @Override
            public void onResponse(Call<EVOwner> call, Response<EVOwner> response) {
                dialogBinding.btnSaveProfile.setEnabled(true);
                dialogBinding.btnSaveProfile.setText("Save");

                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    updateUIWithUserData(currentUser);
                    dialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Profile updated successfully");
                } else {
                    Log.e(TAG, "Failed to update profile: " + response.code());
                    Toast.makeText(ProfileActivity.this, "Failed to update profile: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<EVOwner> call, Throwable t) {
                dialogBinding.btnSaveProfile.setEnabled(true);
                dialogBinding.btnSaveProfile.setText("Save");
                Log.e(TAG, "Network error while updating profile", t);
                Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}