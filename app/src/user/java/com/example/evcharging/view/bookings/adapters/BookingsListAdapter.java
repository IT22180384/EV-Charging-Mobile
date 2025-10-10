package com.example.evcharging.view.bookings.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evcharging.databinding.ItemBookingCardBinding;
import com.example.evcharging.model.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookingsListAdapter extends RecyclerView.Adapter<BookingsListAdapter.BookingViewHolder> {

    public interface OnBookingActionListener {
        void onBookingSelected(@NonNull Booking booking);
        void onModify(@NonNull Booking booking);
        void onCancel(@NonNull Booking booking);
    }

    private final List<Booking> bookings;
    private final OnBookingActionListener actionListener;

    public BookingsListAdapter(@NonNull List<Booking> bookings, OnBookingActionListener actionListener) {
        this.bookings = new ArrayList<>(Objects.requireNonNull(bookings));
        this.actionListener = actionListener;
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
        holder.bind(bookings.get(position), actionListener);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void updateBookings(@NonNull List<Booking> newBookings) {
        bookings.clear();
        bookings.addAll(newBookings);
        notifyDataSetChanged();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

        private final ItemBookingCardBinding binding;

        BookingViewHolder(@NonNull ItemBookingCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Booking booking, OnBookingActionListener actionListener) {
            binding.textStatus.setText(booking.getStatusLabel());
            binding.textBookingId.setText(booking.getBookingId());
            binding.textTitle.setText(booking.getStationName());
            binding.textLocation.setText(booking.getStationAddress());
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

            binding.getRoot().setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onBookingSelected(booking);
                }
            });

            boolean showActions = (booking.canModify() || booking.canCancel());
            binding.containerActions.setVisibility(showActions ? View.VISIBLE : View.GONE);

            binding.buttonModify.setVisibility(booking.canModify() ? View.VISIBLE : View.GONE);
            binding.buttonCancel.setVisibility(booking.canCancel() ? View.VISIBLE : View.GONE);

            binding.buttonModify.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onModify(booking);
                }
            });

            binding.buttonCancel.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onCancel(booking);
                }
            });
        }
    }
}
