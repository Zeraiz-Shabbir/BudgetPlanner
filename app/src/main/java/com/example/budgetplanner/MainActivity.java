package com.example.budgetplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    private Spinner numberDropDown;
    private Spinner timeDropDown;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_income_expense);

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