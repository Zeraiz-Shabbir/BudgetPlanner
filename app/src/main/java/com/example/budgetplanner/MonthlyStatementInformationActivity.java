package com.example.budgetplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MonthlyStatementInformationActivity extends AppCompatActivity {

    // TODO
    // Add logic to handle monthly statement tables and/or savings/limits resetting each month
    // Add logic to handle saving monthly statement information, etc. to the correct month

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_statement_information);

        // Retrieve the selected month and year from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String selectedMonth = intent.getStringExtra("month");
            int selectedYear = intent.getIntExtra("year", 0);

            // Set the text for the monthYearText TextView
            TextView monthYearText = findViewById(R.id.monthYearText);
            monthYearText.setText(selectedMonth + " " + selectedYear);

            // Use the selectedMonth and selectedYear to load and display detailed information
            // for the selected month, including charts and other data.
        }
    }
}
