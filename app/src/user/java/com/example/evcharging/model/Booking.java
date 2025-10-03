package com.example.evcharging.model;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.example.evcharging.R;

public class Booking {

    public enum Status {
        PENDING(R.color.apple_warning, R.drawable.bg_booking_status_pending),
        APPROVED(R.color.apple_success, R.drawable.bg_booking_status_approved),
        COMPLETED(R.color.apple_success, R.drawable.bg_booking_status_completed),
        CANCELLED(R.color.apple_error, R.drawable.bg_booking_status_cancelled);

        private final int colorRes;
        private final int backgroundRes;

        Status(@ColorRes int colorRes, @DrawableRes int backgroundRes) {
            this.colorRes = colorRes;
            this.backgroundRes = backgroundRes;
        }

        public int getColorRes() {
            return colorRes;
        }

        public int getBackgroundRes() {
            return backgroundRes;
        }
    }

    private final String bookingId;
    private final String title;
    private final String location;
    private final String date;
    private final String timeRange;
    private final String slot;
    private final Status status;
    private final String statusLabel;

    public Booking(String bookingId,
                   String title,
                   String location,
                   String date,
                   String timeRange,
                   String slot,
                   Status status,
                   String statusLabel) {
        this.bookingId = bookingId;
        this.title = title;
        this.location = location;
        this.date = date;
        this.timeRange = timeRange;
        this.slot = slot;
        this.status = status;
        this.statusLabel = statusLabel;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public String getSlot() {
        return slot;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }
}
