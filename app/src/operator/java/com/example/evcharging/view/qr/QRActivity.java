package com.example.evcharging.view.qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.evcharging.R;
import com.example.evcharging.view.base.OperatorBaseActivity;

public class QRActivity extends OperatorBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the QR content into the content container
        loadQRContent();
    }
    
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_base;
    }
    
    @Override
    protected String getActivityTitle() {
        return "QR Scanner";
    }
    
    @Override
    protected void setSelectedNavigationItem() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_qr);
        }
    }
    
    private void loadQRContent() {
        // Inflate the QR layout into the content container
        FrameLayout contentContainer = findViewById(R.id.content_container);
        LayoutInflater.from(this).inflate(R.layout.activity_qr, contentContainer, true);
    }
}
