package com.example.evcharging.viewmodel;

import com.example.evcharging.repository.BookingRepository;
import com.example.evcharging.http.dto.ReservationCreateRequest;
import com.example.evcharging.http.dto.ReservationResponse;

public class BookingViewModel extends BaseViewModel {
    
    public interface BookingCallback {
        void onReservationSuccess(ReservationResponse response);
        void onReservationError(String errorMessage);
    }

    private BookingRepository repository;

    public BookingViewModel() {
        repository = new BookingRepository();
    }

    public void createReservation(String userId, String stationId, String startTime,
                                String endTime, String notes, BookingCallback callback) {
        ReservationCreateRequest request = new ReservationCreateRequest(
            userId, stationId, startTime, endTime, notes);

        repository.createReservation(request, new BookingRepository.ReservationCallback() {
            @Override
            public void onSuccess(ReservationResponse response) {
                callback.onReservationSuccess(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onReservationError(errorMessage);
            }
        });
    }
}