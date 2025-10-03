package com.example.evcharging.view.bookings.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evcharging.R;
import com.example.evcharging.model.Booking;

import java.util.List;

public class BookingsListAdapter extends RecyclerView.Adapter<BookingsListAdapter.BookingViewHolder> {

    private final List<Booking> bookings;

    public BookingsListAdapter(@NonNull List<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_card, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookings.get(position));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {

        private final View statusContainer;
        private final View statusDot;
        private final TextView statusText;
        private final TextView bookingIdText;
        private final TextView titleText;
        private final TextView locationText;
        private final TextView dateText;
        private final TextView timeText;
        private final TextView slotText;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            statusContainer = itemView.findViewById(R.id.container_status);
            statusDot = itemView.findViewById(R.id.view_status_dot);
            statusText = itemView.findViewById(R.id.text_status);
            bookingIdText = itemView.findViewById(R.id.text_booking_id);
            titleText = itemView.findViewById(R.id.text_title);
            locationText = itemView.findViewById(R.id.text_location);
            dateText = itemView.findViewById(R.id.text_date);
            timeText = itemView.findViewById(R.id.text_time);
            slotText = itemView.findViewById(R.id.text_slot);
        }

        void bind(Booking booking) {
            statusText.setText(booking.getStatusLabel());
            bookingIdText.setText(booking.getBookingId());
            titleText.setText(booking.getTitle());
            locationText.setText(booking.getLocation());
            dateText.setText(booking.getDate());
            timeText.setText(booking.getTimeRange());
            slotText.setText(booking.getSlot());

            int color = ContextCompat.getColor(itemView.getContext(), booking.getStatus().getColorRes());
            statusText.setTextColor(color);
            statusContainer.setBackgroundResource(booking.getStatus().getBackgroundRes());

            Drawable background = statusDot.getBackground();
            if (background != null) {
                Drawable dotBackground = DrawableCompat.wrap(background.mutate());
                DrawableCompat.setTint(dotBackground, color);
                statusDot.setBackground(dotBackground);
            }
        }
    }
}
