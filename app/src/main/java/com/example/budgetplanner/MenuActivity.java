package com.example.budgetplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetplanner.database.DataSource;

import java.time.LocalDate;
import java.time.ZoneId;

public class MenuActivity extends AppCompatActivity {

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

        // The initial setup is not completed, show the initial setup screen
        setContentView(R.layout.activity_menu);

        Button addIncomeButton = findViewById(R.id.initialAddIncome);
        Button addExpenseButton = findViewById(R.id.initialAddExpense);
        Button budgetingButton = findViewById(R.id.initialBudgeting);
        Button submitButton = findViewById(R.id.initalSubmit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Proceed to the main page
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish this activity so the user cannot go back to it
            }
        });

        // Set click listeners for other buttons (addIncomeButton, addExpenseButton, budgetingButton)
        addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AddIncomeExpenseActivity.class);
                intent.putExtra(INTENT_ISEXPENSE_NAME, false);
                intent.putExtra(INTENT_ISINCOME_NAME, true);
                startActivity(intent);
            }
        });

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AddIncomeExpenseActivity.class);
                intent.putExtra(INTENT_ISEXPENSE_NAME, true);
                intent.putExtra(INTENT_ISINCOME_NAME, false);
                startActivity(intent);
            }
        });

        budgetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, BudgetingActivity.class);
                intent.putExtra(BudgetingActivity.GET_MONTH_FROM_INTENT, MenuActivity.this.currentMonth.toString());
                startActivity(intent);
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
