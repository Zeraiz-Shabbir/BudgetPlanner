package com.example.budgetplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {
    private List<MonthItem> monthList;
    private OnItemClickListener onItemClickListener;

    public MonthAdapter(List<MonthItem> monthList) {
        this.monthList = monthList;
    }

    // Setter method to set the click listener from outside the adapter
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_card, parent, false);
        return new MonthViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        MonthItem currentItem = monthList.get(position);
        holder.monthTextView.setText(currentItem.getMonthName() + " " + currentItem.getYear());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected month and year
                String selectedMonth = currentItem.getMonthName();
                int selectedYear = currentItem.getYear();

                // Check if the listener is set and notify it
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(selectedMonth, selectedYear);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }

    public static class MonthViewHolder extends RecyclerView.ViewHolder {
        public TextView monthTextView;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            monthTextView = itemView.findViewById(R.id.monthText);
        }
    }

    // Interface for item click events
    public interface OnItemClickListener {
        void onItemClick(String selectedMonth, int selectedYear);
    }
}
