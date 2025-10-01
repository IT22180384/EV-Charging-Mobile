package com.example.evcharging.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evcharging.auth.R;
import com.example.evcharging.view.onboarding.WelcomeActivity;
import android.util.Log;

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
        showLoadingState();
        btnLogin.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingState();
                navigateToHome();
            }
        }, 300);
    }

    private void showLoadingState() {
        btnLogin.setText("Signing In...");
        btnLogin.setEnabled(false);
    }

    private void hideLoadingState() {
        btnLogin.setText("Login");
        btnLogin.setEnabled(true);
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void navigateToHome() {
        Class<?> destination = resolveDestinationActivity();
        if (destination == null) {
            Toast.makeText(this, "Unable to determine destination screen", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, destination);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private Class<?> resolveDestinationActivity() {
        final String applicationId = getApplicationContext().getPackageName();

        try {
            if (applicationId.endsWith(".operator")) {
                return Class.forName("com.example.evcharging.view.main.OperatorMainActivity");
            }
            if (applicationId.endsWith(".user")) {
                return Class.forName("com.example.evcharging.view.main.MainActivity");
            }
        } catch (ClassNotFoundException exception) {
            Log.e("LoginActivity", "Destination activity not found for package: " + applicationId, exception);
        }

        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
