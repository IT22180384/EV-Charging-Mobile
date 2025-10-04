package com.example.evcharging.view.bookings;

public interface BookingActionListener {
    void goBackToStepOne();
    void navigateToStepTwo(String stationId, String stationName);
    void navigateToStepThree(String stationId, String stationName, String date, String time);
    void goBackToStepTwo(String stationId, String stationName);
    void completeBooking();
    void cancelBooking();

    void onBookingCancelled(String bookingId);
    void onBookingModified(String bookingId);
    void navigateToModifyBooking();
    void navigateToCancelBooking();
    void navigateToBookingDetails();
}