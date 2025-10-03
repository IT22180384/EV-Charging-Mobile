package com.example.evcharging.view.bookings.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.evcharging.view.bookings.fragments.HistoryBookingsFragment;
import com.example.evcharging.view.bookings.fragments.UpcomingBookingsFragment;

public class BookingsPagerAdapter extends FragmentStateAdapter {

    public BookingsPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new UpcomingBookingsFragment();
        }
        return new HistoryBookingsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
