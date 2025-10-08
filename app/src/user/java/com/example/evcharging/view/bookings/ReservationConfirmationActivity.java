package com.example.evcharging.view.bookings;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evcharging.R;
import com.example.evcharging.utils.QrUtils;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationConfirmationActivity extends AppCompatActivity {
    public static final String EXTRA_STATION_NAME = "station_name";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_RES_ID = "reservation_id";
    public static final String EXTRA_QR = "qr";
    public static final String EXTRA_OPERATOR_ID = "operator_id";

    private Bitmap qrBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_confirmation);

        TextView title = findViewById(R.id.text_title);
        TextView date = findViewById(R.id.text_date);
        TextView time = findViewById(R.id.text_time);
        TextView bookingId = findViewById(R.id.text_booking_id);
        ImageView qrView = findViewById(R.id.image_qr);
        TextView operatorText = findViewById(R.id.text_operator);

        Intent i = getIntent();
        String stationName = i.getStringExtra(EXTRA_STATION_NAME);
        String d = i.getStringExtra(EXTRA_DATE);
        String t = i.getStringExtra(EXTRA_TIME);
        String id = i.getStringExtra(EXTRA_RES_ID);
        String qr = i.getStringExtra(EXTRA_QR);
        String operatorId = i.getStringExtra(EXTRA_OPERATOR_ID);

        title.setText(stationName);
        date.setText(d);
        time.setText(t);
        bookingId.setText(id);
        if (operatorText != null) {
            operatorText.setText(operatorId != null ? operatorId : "-");
        }

        try {
            qrBitmap = QrUtils.generate(qr != null ? qr : id, 800);
            qrView.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Failed to generate QR", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.btnDownloadQr).setOnClickListener(v -> saveQrToGallery());
        findViewById(R.id.btnDone).setOnClickListener(v -> finish());
    }

    private void saveQrToGallery() {
        if (qrBitmap == null) return;
        String fileName = "reservation_qr_" + System.currentTimeMillis() + ".png";
        OutputStream out = null;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                out = getContentResolver().openOutputStream(uri);
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    getContentResolver().update(uri, values, null, null);
                }
                Toast.makeText(this, "QR saved to gallery", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save QR", Toast.LENGTH_SHORT).show();
        } finally {
            if (out != null) try { out.close(); } catch (IOException ignored) {}
        }
    }
}
