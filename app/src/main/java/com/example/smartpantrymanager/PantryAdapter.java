package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    // True if the date (yyyy-MM-dd) is today, past, or within the next 3 days
    private boolean isExpiringSoon(String expiry) {
        if (expiry == null || expiry.isEmpty()) return false;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expiry);
            long daysLeft = (date.getTime() - System.currentTimeMillis()) / (24L * 60 * 60 * 1000);
            return daysLeft <= 3;
        } catch (java.text.ParseException e) {
            return false;
        }
    }

    public void setItems(List<PantryItem> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.textName.setText(item.getName());

        double q = item.getQuantity();
        String qty = (q == Math.floor(q)) ? String.valueOf((int) q) : String.valueOf(q);
        holder.textQuantity.setText(qty + " " + item.getUnit());

        String expiry = item.getExpiryDate();
        if (expiry == null || expiry.isEmpty()) {
            holder.textExpiry.setText("No expiry date");
        } else {
            holder.textExpiry.setText("Expires: " + expiry);
        }

        boolean alertsOn = holder.itemView.getContext()
                .getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getBoolean("expiry_alerts", true);
        if (alertsOn && isExpiringSoon(expiry)) {
            holder.textExpiry.setTextColor(Color.RED);
            holder.textExpiry.setText("Expires soon: " + expiry);
        } else {
            holder.textExpiry.setTextColor(holder.textQuantity.getTextColors());
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textQuantity, textExpiry;

        ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiry = itemView.findViewById(R.id.textExpiry);
        }
    }
}
