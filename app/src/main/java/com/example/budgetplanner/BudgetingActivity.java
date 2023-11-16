package com.example.budgetplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.time.LocalDate;
import java.time.ZoneId;

public class BudgetingActivity extends AppCompatActivity {

    private String monthTableName;
    private String savingsTableName;
    private BudgetDataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting);
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        this.monthTableName = today.getMonth().toString() + today.getYear();
        this.savingsTableName = monthTableName + "_BUDGETING";
        this.ds = new BudgetDataSource(BudgetingActivity.this, monthTableName, savingsTableName);
        Button savingButton = findViewById(R.id.saving_button);
        Button setLimitButton = findViewById(R.id.set_limit_button);
        ProgressBar currentLimitBar = findViewById(R.id.current_limit_bar);
        ProgressBar currentSavingBar = findViewById(R.id.current_saving_bar);
        EditText savingamount = findViewById(R.id.saving_amount);
        EditText setlimit = findViewById(R.id.set_limit_amount);
        Button submitButton = findViewById(R.id.submit_budgeting);
        savingamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                submitButton.setEnabled(false);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                submitButton.setEnabled(true);

            }
        });
        setlimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                submitButton.setEnabled(false);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                submitButton.setEnabled(true);

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double saving = Double.parseDouble(savingamount.getText().toString());
                double spending_limit = Double.parseDouble(setlimit.getText().toString());
                BudgetingActivity.this.ds.editSavings(saving);
                BudgetingActivity.this.ds.editSetLimit(spending_limit);

            }
        });



    }


}