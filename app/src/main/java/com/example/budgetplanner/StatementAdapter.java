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

    private List<Statement> statements;

    public StatementAdapter(List<Statement> statements) {
        this.statements = statements;
    }

    @NonNull
    @Override
    public StatementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_statement, parent, false);
        return new StatementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatementViewHolder holder, int position) {
        Statement statement = statements.get(position);
        holder.bind(statement);
    }

    @Override
    public int getItemCount() {
        return statements.size();
    }

    public class StatementViewHolder extends RecyclerView.ViewHolder {

        private TextView labelTextView;
        private TextView amountTextView;
        private TextView dateTextView;
        private TextView noteTextView;

        public StatementViewHolder(@NonNull View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.labelTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            noteTextView = itemView.findViewById(R.id.noteTextView);
        }

        public void bind(Statement statement) {
            // Set the text for each TextView based on the Statement object
            labelTextView.setText(statement.getLabel());
            amountTextView.setText("amount: " + (statement.isExpense() ? "-$" : "+$") + statement.getAmount());
//            amountTextView.setText("Amount: $" + statement.getAmount());
            dateTextView.setText(statement.getDate());
            noteTextView.setText(statement.getNotes());
        }
    }

}