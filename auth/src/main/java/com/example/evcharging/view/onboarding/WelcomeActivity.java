package com.example.evcharging.view.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evcharging.auth.R;
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
        setContentView(R.layout.activity_welcome);

        initializeViews();
        setupClickListeners();
        startAnimations();
    }

    private void initializeViews() {
        btnGetStarted = findViewById(R.id.btn_login);
    }

    private void setupClickListeners() {
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMainApp();
            }
        });
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
}
