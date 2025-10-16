package com.example.evcharging.model;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.example.evcharging.R;

import java.io.Serializable;

public class Booking implements Serializable {

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

    private final String id;
    private final String bookingId;
    private final String reservationId;
    private final String stationId;
    private final String stationName;
    private final String stationAddress;
    private final String date;
    private final String timeRange;
    private final String slot;
    private final Status status;
    private final String statusLabel;
    private final boolean canModify;
    private final boolean canCancel;
    private final String startTimeIso;
    private final String endTimeIso;

    public Booking(String id,
                   String bookingId,
                   String reservationId,
                   String stationId,
                   String stationName,
                   String stationAddress,
                   String date,
                   String timeRange,
                   String slot,
                   Status status,
                   String statusLabel,
                   boolean canModify,
                   boolean canCancel,
                   String startTimeIso,
                   String endTimeIso) {
        this.id = id;
        this.bookingId = bookingId;
        this.reservationId = reservationId;
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationAddress = stationAddress;
        this.date = date;
        this.timeRange = timeRange;
        this.slot = slot;
        this.status = status;
        this.statusLabel = statusLabel;
        this.canModify = canModify;
        this.canCancel = canCancel;
        this.startTimeIso = startTimeIso;
        this.endTimeIso = endTimeIso;
    }

    public String getId() {
        return id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStationAddress() {
        return stationAddress;
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

    public boolean canModify() {
        return canModify;
    }

    public boolean canCancel() {
        return canCancel;
    }

    public String getStartTimeIso() {
        return startTimeIso;
    }

    public String getEndTimeIso() {
        return endTimeIso;
    }
}