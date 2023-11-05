package com.example.budgetplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String monthTableName = "nov2023";
        String savingsTableName = "nov2023_budgeting";
        BudgetDataSource dataSource = new BudgetDataSource(this, monthTableName, savingsTableName);

        String date = "11/2/23";
        String label = "EXAMPLE TRANSACTION";
        Double amount = 5000.00;
        String notes = "This is an example transaction.";

        dataSource.addStatement(date, label, amount, notes);

        Double currBalance = 4000.00;
        Double setLimit = 1000.00;
        Double savings = 200.00;

        dataSource.addBudgeting(currBalance, setLimit, savings);

        dataSource.close();
    }
}