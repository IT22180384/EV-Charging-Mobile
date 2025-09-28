package com.example.evcharging.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evcharging.R;
import com.example.evcharging.view.onboarding.WelcomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

/**
 * Login Activity with Apple-inspired design
 * Clean card-style layout with smooth animations
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private MaterialTextView tvSignUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
        startAnimations();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUpLink = findViewById(R.id.tv_signup_link);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        tvSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUp();
            }
        });
    }

    private void startAnimations() {
        // Slide in animation for the card
        Animation slideInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slideInAnimation.setDuration(600);
        slideInAnimation.setStartOffset(200);

        View cardView = findViewById(R.id.login_card);
        if (cardView != null) {
            cardView.startAnimation(slideInAnimation);
        }
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            // Simulate login process
            showLoadingState();
            
            // Simulate network delay
            btnLogin.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideLoadingState();
                    showSuccessMessage();
                    // In a real app, you would navigate to the main dashboard here
                    // navigateToMainDashboard();
                }
            }, 2000);
        }
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email or NIC is required");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void showLoadingState() {
        btnLogin.setText("Signing In...");
        btnLogin.setEnabled(false);
    }

    private void hideLoadingState() {
        btnLogin.setText("Login");
        btnLogin.setEnabled(true);
    }

    private void showSuccessMessage() {
        Toast.makeText(this, "Login successful! Welcome to EV Charging ERP System", Toast.LENGTH_LONG).show();
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}