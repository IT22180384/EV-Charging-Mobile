package com.example.evcharging.view.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evcharging.auth.R;
import com.example.evcharging.utils.SpUtil;
import com.example.evcharging.view.auth.LoginActivity;
import com.google.android.material.button.MaterialButton;

/**
 * Welcome/Onboarding Activity for EV Charging ERP System
 * Minimalistic Apple-inspired design with direct navigation to main app
 */
public class WelcomeActivity extends AppCompatActivity {

    private MaterialButton btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SpUtil.isLoggedIn()) {
            navigateBasedOnRole();
        } else {
            setContentView(R.layout.activity_welcome);

            initializeViews();
            setupClickListeners();
            startAnimations();
        }
    }

    private void initializeViews() {
        btnGetStarted = findViewById(R.id.btn_login);
    }

    private void setupClickListeners() {
        btnGetStarted.setOnClickListener(v -> navigateToMainApp());
    }

    private void startAnimations() {
        // Fade in animation for button
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeInAnimation.setDuration(800);
        fadeInAnimation.setStartOffset(500);

        btnGetStarted.startAnimation(fadeInAnimation);
    }

    private void navigateToMainApp() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void navigateBasedOnRole() {
        try {
            String userRole = SpUtil.getUserType();
            final String applicationId = getApplicationContext().getPackageName();
            
            android.util.Log.d("WelcomeActivity", "Stored user role: " + userRole);
            android.util.Log.d("WelcomeActivity", "App package: " + applicationId);
            
            Class<?> destination = null;
            
            // If user is StationOperator, navigate to operator main activity
            if ("StationOperator".equals(userRole)) {
                destination = Class.forName("com.example.evcharging.view.main.OperatorMainActivity");
            }
            // For EVOwner or other roles, check app variant
            else if (applicationId.endsWith(".operator")) {
                // Operator app but user is not StationOperator - redirect to login
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            else {
                // Default to regular MainActivity for user app
                destination = Class.forName("com.example.evcharging.view.main.MainActivity");
            }
            
            if (destination != null) {
                Intent intent = new Intent(this, destination);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        } catch (Exception e) {
            // Fallback to login if any error occurs
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
