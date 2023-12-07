package com.example.budgetplanner;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.budgetplanner.database.Budgeting;
import com.example.budgetplanner.database.DataSource;
import com.example.budgetplanner.database.Statement;
import java.util.List;

public class MonthlyStatementInformationActivity extends AppCompatActivity {

    private DataSource ds;
    private ProgressBar currentSavingBar, currentLimitBar;
    private TextView outputPercentSavings, outputPercentSetLimit;

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
            this.ds = new DataSource(MonthlyStatementInformationActivity.this, month);

            currentSavingBar = findViewById(R.id.savingsProgressBar2);
            currentLimitBar = findViewById(R.id.limitProgressBar2);
            this.updateProgressBars();
            this.printSavings();
            this.printSetLimit();

            TextView balance = findViewById(R.id.balanceStmtView);
            balance.setText(String.format("$%.2f", this.ds.getBalance()));

            // Set the text for the monthYearText TextView
            TextView monthYearText = findViewById(R.id.monthYearText);
            monthYearText.setText(selectedMonth + " " + selectedYear);

            // Use the selectedMonth and selectedYear to load and display detailed information
            // for the selected month, including charts and other data.

//          // Budgeting budgeting = this.ds.getBudgeting();
            List<Statement> statements = this.ds.getStatements();

            // Get the container layout
            LinearLayout containerLayout = findViewById(R.id.containerLayout);

            // Dynamically create TextView for each statement
            for (Statement statement : statements) {
                TextView statementTextView = new TextView(this);
                statementTextView.setText(statement.getLabel() + ": $" + statement.getAmount() + " on " + statement.getDate());

                statementTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openEditIncomeExpenseActivity(statement);
                    }

                    private void openEditIncomeExpenseActivity(Statement statement) {
                        Intent inte = new Intent(MonthlyStatementInformationActivity.this, EditIncomeExpenseActivity.class);
                        // Pass relevant data to EditIncomeExpenseActivity using intent extras
                        inte.putExtra("label", statement.getLabel());
                        inte.putExtra("amount", statement.getAmount());
                        inte.putExtra("date", statement.getDate());
                        startActivity(inte);
                    }
                });

                containerLayout.addView(statementTextView);
            }
        }
    }

    private void updateProgressBars() {
        double amountSpentProgress = (this.ds.getAmountSpent() / this.ds.getSpendingLimit()) * 100;
        double amountSavedProgress = (this.ds.getBalance() / this.ds.getSavingLimit()) * 100;
        currentLimitBar.setProgress((int) amountSpentProgress);
        currentSavingBar.setProgress((int) amountSavedProgress);
    }

    public void printSavings() {
        outputPercentSavings = (TextView) findViewById(R.id.savingsPerc);
        double savingsPercentage = ((this.ds.getBalance() / this.ds.getSavingLimit()) * 100);
        outputPercentSavings.setText(String.format("%.2f%%", savingsPercentage));
        textColorChange(savingsPercentage, false);
    }

    public void printSetLimit() {
        outputPercentSetLimit = (TextView) findViewById(R.id.setLimitPerc);
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
}
