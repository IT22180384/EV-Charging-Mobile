package com.example.evcharging.view.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.evcharging.R;
import com.example.evcharging.databinding.ActivitySettingsBinding;
import com.example.evcharging.view.base.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSettingsContent();
        showNavMenu(); // Show navigation for this activity
        setupClickListeners();
        loadSettingsData();
    }

    private void loadSettingsContent() {
        // Load settings content into the base activity's content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        if (contentContainer != null) {
            binding = ActivitySettingsBinding.inflate(getLayoutInflater());
            contentContainer.addView(binding.getRoot());
        }
    }

    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_settings);
        }
    }

    private void setupClickListeners() {
        if (binding == null) return;

        // App Preferences
        binding.itemNotifications.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.notification_settings_coming_soon), Toast.LENGTH_SHORT).show();
        });

        binding.itemTheme.setOnClickListener(v -> {
            showThemeDialog();
        });

        binding.itemLanguage.setOnClickListener(v -> {
            showLanguageDialog();
        });

        // Privacy & Security
        binding.itemChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.change_password_coming_soon), Toast.LENGTH_SHORT).show();
        });

        binding.switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? getString(R.string.biometric_enabled) : getString(R.string.biometric_disabled);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        binding.itemDataPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.data_privacy_coming_soon), Toast.LENGTH_SHORT).show();
        });

        // Support & Information
        binding.itemHelpSupport.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.help_support_coming_soon), Toast.LENGTH_SHORT).show();
        });

        binding.itemContactUs.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.contact_us_coming_soon), Toast.LENGTH_SHORT).show();
        });

        binding.itemTermsPrivacy.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.terms_privacy_coming_soon), Toast.LENGTH_SHORT).show();
        });

        binding.itemAbout.setOnClickListener(v -> {
            showAboutDialog();
        });
    }

    private void loadSettingsData() {
        if (binding == null) return;

        // Set initial values (in a real app, these would come from preferences)
        binding.switchBiometric.setChecked(true);
    }

    private void showThemeDialog() {
        String[] themes = {getString(R.string.theme_light), getString(R.string.theme_dark), getString(R.string.theme_system)};
        int selectedTheme = 0; // Light mode selected by default

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.theme_dialog_title))
                .setSingleChoiceItems(themes, selectedTheme, (dialog, which) -> {
                    // Handle theme selection
                    String selectedThemeName = themes[which];
                    Toast.makeText(this, getString(R.string.selected_prefix) + selectedThemeName, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void showLanguageDialog() {
        String[] languages = {getString(R.string.language_english), getString(R.string.language_spanish), 
                             getString(R.string.language_french), getString(R.string.language_german), 
                             getString(R.string.language_chinese)};
        int selectedLanguage = 0; // English selected by default

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.language_dialog_title))
                .setSingleChoiceItems(languages, selectedLanguage, (dialog, which) -> {
                    // Handle language selection
                    String selectedLanguageName = languages[which];
                    Toast.makeText(this, getString(R.string.selected_prefix) + selectedLanguageName, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.about_title))
                .setMessage(getString(R.string.about_message))
                .setPositiveButton(getString(R.string.btn_ok), null)
                .show();
    }


}