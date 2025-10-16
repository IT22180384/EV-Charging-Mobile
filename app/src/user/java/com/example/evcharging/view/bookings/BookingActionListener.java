package com.example.evcharging.view.bookings;

import com.example.evcharging.model.Booking;

public interface BookingActionListener {
    void goBackToStepOne();
    void navigateToStepTwo(String stationId, String stationName);
    void navigateToStepThree(String stationId, String stationName, String date, String time);
    void goBackToStepTwo(String stationId, String stationName);
    void completeBooking();
    void cancelBooking();

    void onBookingCancelled(String bookingId);
    void onBookingModified(String bookingId);
    void navigateToModifyBooking(Booking booking);
    void navigateToCancelBooking();
    void navigateToCancelBooking(Booking booking);
    void navigateToBookingDetails();
    void navigateToBookingDetails(Booking booking);
}