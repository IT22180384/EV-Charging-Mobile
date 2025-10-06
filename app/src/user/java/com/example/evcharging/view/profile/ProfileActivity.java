package com.example.evcharging.view.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.evcharging.R;
import com.example.evcharging.databinding.ActivityProfileBinding;
import com.example.evcharging.view.base.BaseActivity;

public class ProfileActivity extends BaseActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadProfileContent();
        showNavMenu(); // Show navigation for this activity
        setupClickListeners();
        loadUserData();
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
            Toast.makeText(this, "Edit profile functionality coming soon", Toast.LENGTH_SHORT).show();
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

        // Load user data (in a real app, this would come from API/database)
        binding.textUserName.setText("John Doe");
        binding.textUserEmail.setText("john.doe@example.com");
        binding.textFullName.setText("John Doe");
        binding.textEmail.setText("john.doe@example.com");
        binding.textPhone.setText("+1 (555) 123-4567");
        binding.textJoinedDate.setText("January 15, 2024");

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