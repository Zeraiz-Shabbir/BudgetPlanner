package com.example.budgetplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetplanner.database.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddIncomeExpenseActivity extends AppCompatActivity {

    private Spinner numberDropDown;
    private Spinner timeDropDown;
    private CalendarView calendarView;
    private DataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide keyboard by default so it doesn't automatically focus on EditTexts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_add_income_expense);

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        MonthItem monthItem = new MonthItem(today.getMonth().toString(), today.getYear());
        try {
            this.ds = new DataSource(AddIncomeExpenseActivity.this, monthItem);
        } catch (BudgetingException e) {
            Utils.diagnoseException(AddIncomeExpenseActivity.this, e);
        }

        // Read the extra parameter to determine income or expense
        Intent intent = getIntent();
        //boolean isIncome = intent.getBooleanExtra(InitialSetupActivity.INTENT_ISINCOME_NAME, false);
        boolean isPayment = intent.getBooleanExtra(InitialSetupActivity.INTENT_ISEXPENSE_NAME, false);

        /*
        if (isIncome) {
            // Customize UI and behavior for adding income
            // For example, update the title or labels
        } else {
            // Customize UI and behavior for adding expense
            // For example, update the title or labels
        }
        */

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
        AtomicBoolean dateToday = new AtomicBoolean(false);
        AtomicBoolean oneTimeStatement = new AtomicBoolean(false);

        // Set up a listener for the first radio group
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.onceRadioButton) {
                // Set drop downs to disabled
                numberDropDown.setEnabled(false);
                timeDropDown.setEnabled(false);
                oneTimeStatement.set(true);
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
                dateToday.set(true);
            }
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText labelField = findViewById(R.id.labelEditText);
                EditText amountField = findViewById(R.id.amountEditText);
                CalendarView dateField = findViewById(R.id.calendar);
                EditText notesField = null;
                String label = labelField.getText().toString();
                Double amount = Double.parseDouble(amountField.getText().toString());
                LocalDate dateObj = (dateToday.get()) ? today : Instant.ofEpochMilli(dateField.getDate()).atZone(ZoneId.systemDefault()).toLocalDate();
                String date = dateObj.format(DataSource.DATE_FORMATTER);
                String notes = notesField.getText().toString();
                long sid = new Random().nextLong();
                Statement statement = null;
                if (oneTimeStatement.get()) {
                    statement = new Statement(sid, date, label, amount, notes, isPayment);
                    try {
                        ds.insertStatement(statement);
                    } catch (BudgetingException e) {
                        Utils.diagnoseException(AddIncomeExpenseActivity.this, e);
                    }
                }
                else {
                    int frequency = (int) numberDropDown.getSelectedItem();
                    String time = (String) timeDropDown.getSelectedItem();
                    statement = new RecurringStatement(sid, date, label, amount, notes, isPayment, frequency, time.charAt(0));
                    try {
                        ds.insertRecurringStatement((RecurringStatement) statement);
                    } catch (BudgetingException e) {
                        Utils.diagnoseException(AddIncomeExpenseActivity.this, e);
                    }
                }
                /*
                double balance = ds.getBalance();
                double setLimit = ds.getSetLimit();
                // Field for monitoring how much the user has come closer to spending limit
                double spentAmt = ds.getAmountSpent();
                double minAmtToSave = ds.getSavings();
                 */
                finish();
            }
        });
    }
}
