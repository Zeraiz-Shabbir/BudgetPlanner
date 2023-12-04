package com.example.budgetplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class AddIncomeExpenseActivity extends AppCompatActivity {

    private Spinner numberDropDown;
    private Spinner timeDropDown;
    private CalendarView calendarView;
    private BudgetDataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_expense);

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String monthTableName = today.getMonth().toString() + today.getYear();
        String savingsTableName = monthTableName + "_BUDGETING";
        this.ds = new BudgetDataSource(AddIncomeExpenseActivity.this, monthTableName, savingsTableName);

        // Read the extra parameter to determine income or expense
        Intent intent = getIntent();
        boolean isIncome = intent.getBooleanExtra(InitialSetupActivity.INTENT_ISINCOME_NAME, false);
        boolean isPayment = intent.getBooleanExtra(InitialSetupActivity.INTENT_ISEXPENSE_NAME, false);

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

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Statement stmt = new Statement();
                EditText labelField = findViewById(R.id.labelEditText);
                EditText amountField = findViewById(R.id.amountEditText);
                CalendarView dateField = findViewById(R.id.calendar);
                EditText notesField = null;

                String label = labelField.getText().toString();
                Double amount = Double.parseDouble(amountField.getText().toString());
                LocalDate dateObj = Instant.ofEpochMilli(dateField.getDate()).atZone(ZoneId.systemDefault()).toLocalDate();
                String date = "";
                //String notes = notesField.getText().toString();

                Intent data = getIntent();
                boolean isExpense = data.getBooleanExtra("isExpense", false);
                final boolean[] cancelpayment = {false};
                double balance = ds.getBalance();
                double setLimit = ds.getSetLimit();
                // Field for monitoring how much the user has come closer to spending limit
                double spentAmt = ds.getAmountSpent();
                double minAmtToSave = ds.getSavings();

                if (isExpense) {

                    // Expense would negate balance, hence all savings as well
                    if (balance < amount) {
                        WarningDialogManager.showSavingDepletedDialog(AddIncomeExpenseActivity.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                cancelpayment[0] = true;
                                finish();
                                dialog.dismiss();
                            }
                        });
                    }
                    // Expense would cross the spending limit set previously
                    else if (setLimit < (spentAmt + amount)) {
                        WarningDialogManager.showLimitExceededDialog(AddIncomeExpenseActivity.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                cancelpayment[0] = true;
                                finish();
                                dialog.dismiss();
                            }
                        });
                    }
                    // Expense didn't deplete savings or cross spending limit
                    else {

                        spentAmt += amount;
                        balance -= amount;
                        ds.editAmountSpent(spentAmt);
                        ds.editBalance(balance);
                    }
                }
                else if (isIncome) {

                    balance += amount;
                    ds.editBalance(balance);
                }

                if (!cancelpayment[0]) {


                }

                int month = dateObj.getMonthValue();
                int day = dateObj.getDayOfMonth();
                int year = dateObj.getYear();

                date.concat((month < 10) ? ("0" + month) : String.valueOf(month));
                date.concat((day < 10) ? ("0" + day) : String.valueOf(day));
                date.concat(String.valueOf(year));

                stmt.setDate(date);
                stmt.setLabel(label);
                stmt.setAmount(amount);
                stmt.setNotes(null);

                long id = ds.addStatement(stmt);
                stmt.setId(id);

                finish();
            }
        });
    }
}
