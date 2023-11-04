package com.example.budgetplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AddIncomeExpenseActivity extends AppCompatActivity {

    private Spinner numberDropDown;
    private Spinner timeDropDown;
    private CalendarView calendarView;

    // TODO
    // Fill in logic to correctly handle either adding an expense or an income based on booleans

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide keyboard by default so it doesn't automatically focus on EditTexts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_add_income_expense);

        // Read the extra parameter to determine income or expense
        Intent intent = getIntent();
        boolean isIncome = intent.getBooleanExtra("isIncome", false);

        if (isIncome) {
            // Customize UI and behavior for adding income
            // For example, update the title or labels
        } else {
            // Customize UI and behavior for adding expense
            // For example, update the title or labels
        }

        // Initialize views
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioGroup radioGroup2 = findViewById(R.id.radioGroup2);
        numberDropDown = findViewById(R.id.numberDropDown);
        timeDropDown = findViewById(R.id.timeDropDown);
        calendarView = findViewById(R.id.calendar);

        // Set initial values for views
        numberDropDown.setEnabled(false);
        timeDropDown.setEnabled(false);
        calendarView.setVisibility(View.GONE);

        // Set up a listener for the first radio group
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.onceRadioButton) {
                // Set drop downs to disabled
                numberDropDown.setEnabled(false);
                timeDropDown.setEnabled(false);
            } else if (checkedId == R.id.everyRadioButton) {
                // Set drop downs to enabled
                numberDropDown.setEnabled(true);
                timeDropDown.setEnabled(true);
            }
        });

        // Set up a listener for the second radio group
        radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.dateSelect) {
                // Set calendar to visible
                calendarView.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.dateToday) {
                // Set calendar to gone
                calendarView.setVisibility(View.GONE);
            }
        });
    }
}
