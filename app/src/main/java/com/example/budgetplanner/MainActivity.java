package com.example.budgetplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Hello World");

        String tableName = "example";
        BudgetDataSource dataSource = new BudgetDataSource(this, tableName);
        String date = "11/2/23";
        String label = "EXAMPLE TRANSACTION";
        Double amount = 5000.00;
        String notes = "This is an example transaction.";

        dataSource.addStatement(date, label, amount, notes);
        dataSource.removeStatement(date, label, amount, notes);
        dataSource.closeDatabase();
    }
}