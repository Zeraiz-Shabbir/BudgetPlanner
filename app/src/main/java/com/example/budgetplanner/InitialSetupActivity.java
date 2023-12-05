package com.example.budgetplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetplanner.database.DataSource;

import java.time.LocalDate;
import java.time.ZoneId;

public class InitialSetupActivity extends AppCompatActivity {

    static final String INTENT_ISEXPENSE_NAME = "isExpense";
    static final String INTENT_ISINCOME_NAME = "isIncome";
    private DataSource ds;
    private MonthItem currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String month = today.getMonth().toString();
        int currentYear = today.getYear();
        this.currentMonth = new MonthItem(month, currentYear);
        this.ds = new DataSource(this, this.currentMonth);

        // hide keyboard by default so it doesn't automatically focus on EditTexts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Check if the initial setup has already been completed
        SharedPreferences preferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isInitialSetupCompleted = preferences.getBoolean("isInitialSetupCompleted", false);

        if (isInitialSetupCompleted) {
            // The initial setup has already been completed, so go to the main page
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Finish this activity so the user cannot go back to it
        }
        else {
            // The initial setup is not completed, show the initial setup screen
            setContentView(R.layout.activity_initial_setup);

            Button addIncomeButton = findViewById(R.id.initialAddIncome);
            Button addExpenseButton = findViewById(R.id.initialAddExpense);
            Button budgetingButton = findViewById(R.id.initialBudgeting);
            Button submitButton = findViewById(R.id.initalSubmit);
            EditText startingBalanceEditText = findViewById(R.id.balanceStart);
            Button okButton = findViewById(R.id.saveBalance);

            // Set a click listener for the Submit button
            submitButton.setEnabled(false);
            okButton.setEnabled(false);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if the required fields are filled
                    if (!startingBalanceEditText.getText().toString().isEmpty() //&& Add conditions for budgeting fields here
                    ) {
                        // Mark initial setup as completed
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isInitialSetupCompleted", true);
                        editor.apply();

                        // Proceed to the main page
                        Intent intent = new Intent(InitialSetupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Finish this activity so the user cannot go back to it
                    } else {
                        // Show an error message or prompt to fill out required fields
                    }
                }
            });
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InitialSetupActivity.this.ds.setBalance(Double.parseDouble(startingBalanceEditText.getText().toString()));
                    InitialSetupActivity.this.ds.save();
                    //Toast.makeText(InitialSetupActivity.this, String.valueOf(InitialSetupActivity.this.ds.getBalance()), Toast.LENGTH_SHORT).show();
                }
            });

            // Set a text change listener for the starting balance EditText
            startingBalanceEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    // Enable the Submit button if the starting balance is not empty
                    submitButton.setEnabled(!startingBalanceEditText.getText().toString().isEmpty());
                    okButton.setEnabled(!startingBalanceEditText.getText().toString().isEmpty());
                }
            });

            // Set click listeners for other buttons (addIncomeButton, addExpenseButton, budgetingButton)
            addIncomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InitialSetupActivity.this, AddIncomeExpenseActivity.class);
                    intent.putExtra(INTENT_ISEXPENSE_NAME, false);
                    intent.putExtra(INTENT_ISINCOME_NAME, true);
                    startActivity(intent);
                }
            });

            addExpenseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InitialSetupActivity.this, AddIncomeExpenseActivity.class);
                    intent.putExtra(INTENT_ISEXPENSE_NAME, true);
                    intent.putExtra(INTENT_ISINCOME_NAME, false);
                    startActivity(intent);
                }
            });

            budgetingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InitialSetupActivity.this, BudgetingActivity.class);
                    intent.putExtra(BudgetingActivity.GET_MONTH_FROM_INTENT, InitialSetupActivity.this.currentMonth.toString());
                    startActivity(intent);
                }
            });
        }
    }
}
