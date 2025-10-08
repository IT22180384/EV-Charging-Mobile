package com.example.evcharging.view.bookings.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evcharging.R;
import com.example.evcharging.model.ChargingStation;

import java.util.ArrayList;
import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {

    public interface OnStationClickListener {
        void onStationClick(ChargingStation station);
    }

    private final List<ChargingStation> items = new ArrayList<>();
    private final OnStationClickListener listener;

    public StationAdapter(OnStationClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ChargingStation> stations) {
        items.clear();
        if (stations != null) {
            items.addAll(stations);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChargingStation station = items.get(position);
        holder.title.setText(station.getName());
        holder.subtitle.setText(station.getAddress());
        holder.slots.setText(holder.itemView.getContext().getString(R.string.slots_format, station.getAvailableSlots(), station.getTotalSlots()));
        holder.itemView.setOnClickListener(v -> listener.onStationClick(station));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        TextView slots;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            subtitle = itemView.findViewById(R.id.textSubtitle);
            slots = itemView.findViewById(R.id.textSlots);
        }
    }
}

