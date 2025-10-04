package com.example.evcharging.view.bookings.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evcharging.databinding.ItemBookingCardBinding;
import com.example.evcharging.model.Booking;
import com.example.evcharging.view.bookings.BookingActionListener;

import java.util.List;

public class BookingsListAdapter extends RecyclerView.Adapter<BookingsListAdapter.BookingViewHolder> {

    private final List<Booking> bookings;
    private final BookingActionListener listener;

    public BookingsListAdapter(@NonNull List<Booking> bookings, BookingActionListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingCardBinding binding = ItemBookingCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookings.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

        private final ItemBookingCardBinding binding;

        BookingViewHolder(@NonNull ItemBookingCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Booking booking, BookingActionListener listener) {
            binding.textStatus.setText(booking.getStatusLabel());
            binding.textBookingId.setText(booking.getBookingId());
            binding.textTitle.setText(booking.getTitle());
            binding.textLocation.setText(booking.getLocation());
            binding.textDate.setText(booking.getDate());
            binding.textTime.setText(booking.getTimeRange());
            binding.textSlot.setText(booking.getSlot());

            int color = ContextCompat.getColor(itemView.getContext(), booking.getStatus().getColorRes());
            binding.textStatus.setTextColor(color);
            binding.containerStatus.setBackgroundResource(booking.getStatus().getBackgroundRes());

            Drawable background = binding.viewStatusDot.getBackground();
            if (background != null) {
                Drawable dotBackground = DrawableCompat.wrap(background.mutate());
                DrawableCompat.setTint(dotBackground, color);
                binding.viewStatusDot.setBackground(dotBackground);
            }

            // Set click listener to navigate to booking details
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.navigateToBookingDetails();
                }
            });
        }
    }
}