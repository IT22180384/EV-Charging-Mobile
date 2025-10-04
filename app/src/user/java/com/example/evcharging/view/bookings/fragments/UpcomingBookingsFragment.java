package com.example.evcharging.view.bookings.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.evcharging.databinding.FragmentBookingsListBinding;
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.adapters.BookingsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class UpcomingBookingsFragment extends Fragment {
    private FragmentBookingsListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerBookings.setAdapter(new BookingsListAdapter(createUpcomingBookings()));
    }

    private List<Booking> createUpcomingBookings() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(
                "#BK001234",
                "Downtown Charging Station",
                "123 Liberty Ave, City Center",
                "Dec 15, 2024",
                "2:00 PM - 4:00 PM",
                "Slot A2",
                Booking.Status.PENDING,
                "Pending"
        ));
        bookings.add(new Booking(
                "#BK001235",
                "Mall Charging Hub",
                "456 Central Blvd, North Mall",
                "Dec 18, 2024",
                "10:00 AM - 12:00 PM",
                "Slot B1",
                Booking.Status.APPROVED,
                "Approved"
        ));
        bookings.add(new Booking(
                "#BK001236",
                "Airport Terminal Station",
                "Terminal 2, Level P1",
                "Dec 20, 2024",
                "6:00 AM - 8:00 AM",
                "Slot C3",
                Booking.Status.APPROVED,
                "Approved"
        ));
        return bookings;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}