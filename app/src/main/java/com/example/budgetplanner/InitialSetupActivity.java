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

public class InitialSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide keyboard by default so it doesn't automatically focus on EditTexts
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Check if the initial setup has already been completed
        SharedPreferences preferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isInitialSetupCompleted = preferences.getBoolean("isInitialSetupCompleted", false);

        if (isInitialSetupCompleted) {
            // The initial setup has already been completed, so go to the main page
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Finish this activity so the user cannot go back to it
        } else {

            // The initial setup is not completed, show the initial setup screen
            setContentView(R.layout.activity_initial_setup);

            Button addIncomeButton = findViewById(R.id.initialAddIncome);
            Button addExpenseButton = findViewById(R.id.initialAddExpense);
            Button budgetingButton = findViewById(R.id.initialBudgeting);
            Button submitButton = findViewById(R.id.initalSubmit);
            EditText startingBalanceEditText = findViewById(R.id.balanceStart);

            // Set a click listener for the Submit button
            submitButton.setEnabled(false);
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
                }
            });

            // Set click listeners for other buttons (addIncomeButton, addExpenseButton, budgetingButton)
            addIncomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InitialSetupActivity.this, AddIncomeExpenseActivity.class);
                    intent.putExtra("isIncome", true);
                    startActivity(intent);
                }
            });

            addExpenseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InitialSetupActivity.this, AddIncomeExpenseActivity.class);
                    intent.putExtra("isIncome", false);
                    startActivity(intent);
                }
            });

            budgetingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InitialSetupActivity.this, BudgetingActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
