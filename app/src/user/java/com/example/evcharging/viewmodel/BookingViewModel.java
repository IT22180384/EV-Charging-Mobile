package com.example.evcharging.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import com.example.evcharging.data.repository.UserBookingRepository;
import com.example.evcharging.http.dto.BookingSessionResponse;
import com.example.evcharging.http.dto.ReservationCreateRequest;
import com.example.evcharging.http.dto.ReservationResponse;
import com.example.evcharging.http.dto.ReservationUpdateRequest;
import com.example.evcharging.http.dto.StationDetailResponse;
import com.example.evcharging.model.Booking;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class BookingViewModel extends BaseViewModel {

    public interface BookingCallback {
        void onReservationSuccess(ReservationResponse response);
        void onReservationError(String errorMessage);
    }

    public interface BookingsCallback {
        void onBookingsLoaded(List<Booking> bookings);
        void onError(String errorMessage);
    }

    public interface ReservationUpdateCallback {
        void onUpdateSuccess(ReservationResponse response);
        void onUpdateError(String errorMessage);
    }

    public interface ReservationActionCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    private final UserBookingRepository repository;
    private final SimpleDateFormat apiDateTimeFormatWithMs;
    private final SimpleDateFormat apiDateTimeFormat;
    private final SimpleDateFormat fallbackDateFormat;
    private final SimpleDateFormat displayDateFormat;
    private final SimpleDateFormat displayTimeFormat;
    private final Map<String, StationInfo> stationInfoCache = new ConcurrentHashMap<>();

    public BookingViewModel() {
        repository = new UserBookingRepository();
        apiDateTimeFormatWithMs = createFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        apiDateTimeFormat = createFormat("yyyy-MM-dd'T'HH:mm:ssX");
        fallbackDateFormat = createFormat("yyyy-MM-dd'T'HH:mm:ss");

        displayDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        displayDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        displayTimeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        displayTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void createReservation(String userId, String stationId, String startTime,
                                  String endTime, String notes, BookingCallback callback) {
        ReservationCreateRequest request = new ReservationCreateRequest(
                userId, stationId, startTime, endTime, notes);

        repository.createReservation(request, new UserBookingRepository.ReservationCallback() {
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

    public void updateReservation(String reservationId, String startTime, String endTime,
                                  String status, String notes, String chargingStationId, ReservationUpdateCallback callback) {
        ReservationUpdateRequest request = new ReservationUpdateRequest(startTime, endTime, status, notes, chargingStationId);
        repository.updateReservation(reservationId, request, new UserBookingRepository.ReservationCallback() {
            @Override
            public void onSuccess(ReservationResponse response) {
                callback.onUpdateSuccess(response);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onUpdateError(errorMessage);
            }
        });
    }

    public void cancelReservation(String reservationId, ReservationActionCallback callback) {
        repository.cancelReservation(reservationId, new UserBookingRepository.ReservationActionCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void getPendingBookings(String userId, BookingsCallback callback) {
        repository.getPendingBookings(userId, new UserBookingRepository.BookingSessionsCallback() {
            @Override
            public void onSuccess(List<BookingSessionResponse> sessions) {
                List<Booking> bookings = mapSessionsToBookings(sessions);
                callback.onBookingsLoaded(bookings);
                fetchStationDetailsIfNeeded(sessions, callback);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void getCompletedBookings(String userId, BookingsCallback callback) {
        repository.getCompletedBookings(userId, new UserBookingRepository.BookingSessionsCallback() {
            @Override
            public void onSuccess(List<BookingSessionResponse> sessions) {
                List<Booking> bookings = mapSessionsToBookings(sessions);
                callback.onBookingsLoaded(bookings);
                fetchStationDetailsIfNeeded(sessions, callback);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    private void fetchStationDetailsIfNeeded(List<BookingSessionResponse> sessions, BookingsCallback callback) {
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        Set<String> missingIds = new HashSet<>();
        for (BookingSessionResponse session : sessions) {
            String stationId = session.stationId;
            if (!TextUtils.isEmpty(stationId)) {
                boolean hasName = !TextUtils.isEmpty(session.stationName);
                boolean hasAddress = !TextUtils.isEmpty(session.stationAddress);
                if (!stationInfoCache.containsKey(stationId) && (!hasName || !hasAddress)) {
                    missingIds.add(stationId);
                }
            }
        }

        if (missingIds.isEmpty()) {
            // No missing info, but we may have cached entries to apply
            callback.onBookingsLoaded(mapSessionsToBookings(sessions));
            return;
        }

        List<String> ids = new ArrayList<>(missingIds);
        Collections.sort(ids);
        fetchStationDetailSequential(ids, 0, sessions, callback);
    }

    private void fetchStationDetailSequential(List<String> ids, int index,
                                              List<BookingSessionResponse> sessions,
                                              BookingsCallback callback) {
        if (index >= ids.size()) {
            callback.onBookingsLoaded(mapSessionsToBookings(sessions));
            return;
        }

        String stationId = ids.get(index);
        repository.getStationDetail(stationId, new UserBookingRepository.StationDetailCallback() {
            @Override
            public void onSuccess(StationDetailResponse station) {
                if (station != null) {
                    StationInfo info = new StationInfo(
                            station.name,
                            station.address != null ? station.address :
                                    station.location != null ? station.location.address : null
                    );
                    stationInfoCache.put(stationId, info);
                }
                fetchStationDetailSequential(ids, index + 1, sessions, callback);
            }

            @Override
            public void onError(String errorMessage) {
                Log.w("BookingViewModel", "Failed to fetch station detail for " + stationId + ": " + errorMessage);
                fetchStationDetailSequential(ids, index + 1, sessions, callback);
            }
        });
    }

    private List<Booking> mapSessionsToBookings(List<BookingSessionResponse> sessions) {
        List<Booking> bookings = new ArrayList<>();
        if (sessions == null) {
            return bookings;
        }

        for (BookingSessionResponse session : sessions) {
            String id = valueOrDefault(session.id,
                    valueOrDefault(session.reservationId, session.bookingId));
            String bookingId = valueOrDefault(session.bookingId, "-");
            String reservationId = valueOrDefault(session.reservationId,
                    valueOrDefault(session.id, session.bookingId));
            String stationId = valueOrDefault(session.stationId, "");

            Log.d("BookingViewModel", "Mapping booking - ID: " + session.id +
                    ", ReservationId: " + session.reservationId +
                    ", BookingId: " + session.bookingId +
                    ", Final ID for API calls: " + id);

            StationInfo cachedInfo = !TextUtils.isEmpty(stationId) ? stationInfoCache.get(stationId) : null;
            String stationName = cachedInfo != null && !TextUtils.isEmpty(cachedInfo.name)
                    ? cachedInfo.name
                    : valueOrDefault(session.stationName, stationId);
            String stationAddress = cachedInfo != null && !TextUtils.isEmpty(cachedInfo.address)
                    ? cachedInfo.address
                    : valueOrDefault(session.stationAddress, "");

            Date start = parseDate(session.startTime);
            Date end = parseDate(session.endTime);
            if (start == null && session.reservationDateTime != null) {
                start = parseDate(session.reservationDateTime);
            }

            String date = start != null ? displayDateFormat.format(start) : "";
            String timeRange = buildTimeRange(start, end);
            String slot = valueOrDefault(session.sessionNotes, "Reserved slot");
            Booking.Status status = mapStatus(session.status);
            String statusLabel = formatStatusLabel(session.status, status);
            boolean canModify = status == Booking.Status.PENDING || status == Booking.Status.APPROVED;
            boolean canCancel = status == Booking.Status.PENDING || status == Booking.Status.APPROVED;

            bookings.add(new Booking(
                    id,
                    formatBookingId(bookingId),
                    reservationId,
                    stationId,
                    stationName,
                    stationAddress,
                    date,
                    timeRange,
                    slot,
                    status,
                    statusLabel,
                    canModify,
                    canCancel,
                    session.startTime,
                    session.endTime
            ));
        }
        return bookings;
    }

    private String buildTimeRange(Date start, Date end) {
        if (start == null) {
            return "";
        }
        String startText = displayTimeFormat.format(start);
        String endText;
        if (end != null) {
            endText = displayTimeFormat.format(end);
        } else {
            long oneHourLater = start.getTime() + 60 * 60 * 1000;
            endText = displayTimeFormat.format(new Date(oneHourLater));
        }
        return startText + " - " + endText;
    }

    private Booking.Status mapStatus(String status) {
        if (status == null) {
            return Booking.Status.PENDING;
        }
        String normalized = status.trim().toLowerCase(Locale.US);
        switch (normalized) {
            case "approved":
            case "confirmed":
            case "inprogress":
            case "in_progress":
            case "active":
                return Booking.Status.APPROVED;
            case "completed":
                return Booking.Status.COMPLETED;
            case "cancelled":
            case "canceled":
                return Booking.Status.CANCELLED;
            default:
                return Booking.Status.PENDING;
        }
    }

    private String formatStatusLabel(String status, Booking.Status mappedStatus) {
        if (!TextUtils.isEmpty(status)) {
            String trimmed = status.trim();
            if (!trimmed.isEmpty()) {
                return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1).toLowerCase(Locale.US);
            }
        }
        switch (mappedStatus) {
            case APPROVED:
                return "Approved";
            case COMPLETED:
                return "Completed";
            case CANCELLED:
                return "Cancelled";
            default:
                return "Pending";
        }
    }

    private String formatBookingId(String bookingId) {
        if (TextUtils.isEmpty(bookingId) || "-".equals(bookingId)) {
            return "-";
        }
        return "#" + bookingId.trim();
    }

    private String valueOrDefault(String value, String defaultValue) {
        return value != null && !value.trim().isEmpty() ? value : defaultValue;
    }

    private SimpleDateFormat createFormat(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }

    private Date parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return apiDateTimeFormatWithMs.parse(value);
        } catch (ParseException ignore) {
            try {
                return apiDateTimeFormat.parse(value);
            } catch (ParseException ignored) {
                try {
                    return fallbackDateFormat.parse(value);
                } catch (ParseException e) {
                    return null;
                }
            }
        }
    }

    private static class StationInfo {
        final String name;
        final String address;

        StationInfo(String name, String address) {
            this.name = name;
            this.address = address;
        }
    }
}