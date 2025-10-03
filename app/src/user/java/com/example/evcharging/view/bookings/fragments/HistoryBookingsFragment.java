package com.example.evcharging.view.bookings.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evcharging.R;
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.adapters.BookingsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoryBookingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookings_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_bookings);
        recyclerView.setAdapter(new BookingsListAdapter(createHistoryBookings()));
    }

    private List<Booking> createHistoryBookings() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(
                "#BK001201",
                "Harbor EV Plaza",
                "7 Seaside Way, Downtown",
                "Nov 28, 2024",
                "9:00 AM - 11:00 AM",
                "Slot D4",
                Booking.Status.COMPLETED,
                "Completed"
        ));
        bookings.add(new Booking(
                "#BK001198",
                "Greenfield Charging Park",
                "Sunset Blvd, Uptown",
                "Nov 14, 2024",
                "3:00 PM - 5:00 PM",
                "Slot A1",
                Booking.Status.CANCELLED,
                "Cancelled"
        ));
        return bookings;
    }
}
