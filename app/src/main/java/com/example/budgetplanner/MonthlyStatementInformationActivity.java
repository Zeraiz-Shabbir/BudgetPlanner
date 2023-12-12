package com.example.budgetplanner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetplanner.database.BudgetingException;
import com.example.budgetplanner.database.DataSource;
import com.example.budgetplanner.database.Statement;
import com.example.budgetplanner.database.Utils;

import java.util.List;

public class MonthlyStatementInformationActivity extends AppCompatActivity {

    private DataSource ds;
    private ProgressBar currentSavingBar, currentLimitBar;
    private TextView outputPercentSavings, outputPercentSetLimit;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_statement_information);

        // Retrieve the selected month and year from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String selectedMonth = intent.getStringExtra("month");
            int selectedYear = intent.getIntExtra("year", 0);
            MonthItem month = new MonthItem(selectedMonth.toUpperCase(), selectedYear);
            try {
                this.ds = new DataSource(MonthlyStatementInformationActivity.this, month);
            } catch (BudgetingException e) {
                Utils.diagnoseException(this, e);
            }

            currentSavingBar = findViewById(R.id.savingsProgressBar2);
            currentLimitBar = findViewById(R.id.limitProgressBar2);
//            this.updateProgressBars();
//            this.printSavings();
//            this.printSetLimit();

            TextView balance = findViewById(R.id.balanceStmtView);
            balance.setText(String.format("$%.2f", this.ds.getBalance()));

            // Set the text for the monthYearText TextView
            TextView monthYearText = findViewById(R.id.monthYearText);
            monthYearText.setText(selectedMonth + " " + selectedYear);

            // Use the selectedMonth and selectedYear to load and display detailed information
            // for the selected month, including charts and other data.

            List<Statement> statements = this.ds.getStatements();

            // Get the RecyclerView
            recyclerView = findViewById(R.id.recyclerViewMain);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Use the adapter to populate the RecyclerView
            StatementAdapter adapter = new StatementAdapter(statements);
            recyclerView.setAdapter(adapter);

            // Log container layout visibility
            Log.d("ContainerLayoutVisibility", "Is Visible: " + (recyclerView.getVisibility() == View.VISIBLE));

            // Button functionButton is now outside the loop
            Button functionButton = findViewById(R.id.functionButton);
            functionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Proceed to the menu page
                    Intent menuIntent = new Intent(MonthlyStatementInformationActivity.this, MenuActivity.class);
                    startActivity(menuIntent);
                    finish(); // Finish this activity so the user cannot go back to it
                }
            });

        }
    }

    private void updateProgressBars() {
        double amountSpentProgress = (this.ds.getAmountSpent() / this.ds.getSpendingLimit()) * 100;
        double amountSavedProgress = (this.ds.getBalance() / this.ds.getSavingLimit()) * 100;
        currentLimitBar.setProgress((int) amountSpentProgress);
        currentSavingBar.setProgress((int) amountSavedProgress);
    }

    public void printSavings() {
        outputPercentSavings = findViewById(R.id.savingsPerc);
        double savingsPercentage = ((this.ds.getBalance() / this.ds.getSavingLimit()) * 100);
        outputPercentSavings.setText(String.format("%.2f%%", savingsPercentage));
        textColorChange(savingsPercentage, false);
    }

    public void printSetLimit() {
        outputPercentSetLimit = findViewById(R.id.setLimitPerc);
        double setLimitPercentage = ((this.ds.getAmountSpent() / this.ds.getSpendingLimit()) * 100);
        outputPercentSetLimit.setText(String.format("%.2f%%", setLimitPercentage));
        textColorChange(setLimitPercentage, true);
    }

    private void textColorChange(double percentage, boolean isSetLimit) {
        if (isSetLimit) {
            if (percentage >= 0.00 && percentage <= 50.99) {
                outputPercentSetLimit.setTextColor(Color.GREEN);
                currentLimitBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 51.00 && percentage <= 75.99) {
                outputPercentSetLimit.setTextColor(Color.parseColor("#FFFDD835"));
                currentLimitBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFFDD835"), PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 76.00 && percentage <= 85.99) {
                outputPercentSetLimit.setTextColor(Color.parseColor("#FFA500"));
                currentLimitBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFA500"), PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 86.00) {
                outputPercentSetLimit.setTextColor(Color.RED);
                currentLimitBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            }
        }

        if (!isSetLimit) {
            if (percentage >= 0.00 && percentage <= 24.99) {
                outputPercentSavings.setTextColor(Color.RED);
                currentSavingBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 25.00 && percentage <= 49.99) {
                outputPercentSavings.setTextColor(Color.parseColor("#FFA500"));
                currentSavingBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFA500"), PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 50.00 && percentage <= 74.99) {
                outputPercentSavings.setTextColor(Color.parseColor("#FFFDD835"));
                currentSavingBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFFDD835"), PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 75.00) {
                outputPercentSavings.setTextColor(Color.GREEN);
                currentSavingBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {

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
                labelTextView.setText(statement.getLabel());
                String amountSign = statement.isExpense() ? "-$" : "+$";
                amountTextView.setText("amount: " + amountSign + statement.getAmount());
                dateTextView.setText(statement.getDate());
                noteTextView.setText(statement.getNotes());
            }
        }
    }
}
