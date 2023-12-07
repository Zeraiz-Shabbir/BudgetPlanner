package com.example.budgetplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetplanner.database.Statement;

import java.util.List;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {

    private List<Statement> statementList;

    public StatementAdapter(List<Statement> statementList) {
        this.statementList = statementList;
    }

    @NonNull
    @Override
    public StatementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statement, parent, false);
        return new StatementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StatementViewHolder holder, int position) {
        Statement statement = statementList.get(position);

        holder.labelTextView.setText(statement.getLabel());
        holder.amountTextView.setText(String.format("$%.2f", statement.getAmount()));
        holder.dateTextView.setText(statement.getDate());
    }

    @Override
    public int getItemCount() {
        return statementList.size();
    }

    public static class StatementViewHolder extends RecyclerView.ViewHolder {
        public TextView labelTextView;
        public TextView amountTextView;
        public TextView dateTextView;

        public StatementViewHolder(@NonNull View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.labelTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
