package com.example.budgetplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.ZoneId;

public class BudgetingActivity extends AppCompatActivity {

    private String monthTableName;
    private String savingsTableName;
    private BudgetDataSource ds;
    private ProgressBar currentSavingBar;
    private ProgressBar currentLimitBar;
    private EditText savingAmountEditText;
    private EditText setLimitAmountEditText;

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
        currentSavingBar = findViewById(R.id.current_saving_bar);
        currentLimitBar = findViewById(R.id.current_limit_bar);
        savingAmountEditText = findViewById(R.id.saving_amount);
        setLimitAmountEditText = findViewById(R.id.set_limit_amount);
        Button submitButton = findViewById(R.id.submit_budgeting);

        savingAmountEditText.addTextChangedListener(new TextWatcher() {
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

        setLimitAmountEditText.addTextChangedListener(new TextWatcher() {
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
                double saving = Double.parseDouble(savingAmountEditText.getText().toString());
                double spending_limit = Double.parseDouble(setLimitAmountEditText.getText().toString());
                ds.editSavings(saving);
                ds.editSetLimit(spending_limit);

                // Update the progress bars
                updateProgressBars();
            }
        });

        savingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavingInputDialog();
            }
        });

        setLimitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetLimitInputDialog();
            }
        });
    }

    private void showSavingInputDialog() {
        SavingAndSetLimitDialogManager.showSavingInputDialog(this, new SavingAndSetLimitDialogManager.OnAmountEnteredListener() {
            @Override
            public void onAmountEntered(String amount) {
                showToast("Saving Amount: " + amount);
                // You can perform further actions with the saving amount
            }
        });
    }

    private void showSetLimitInputDialog() {
        SavingAndSetLimitDialogManager.showSetLimitInputDialog(this, new SavingAndSetLimitDialogManager.OnAmountEnteredListener() {
            @Override
            public void onAmountEntered(String amount) {
                showToast("Set Limit Amount: " + amount);
                // You can perform further actions with the set limit amount
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateProgressBars() {
        double currentSaving = ds.getSavings();
        double currentLimit = ds.getSetLimit();

        int progressSaving = (int) ((currentSaving / currentLimit) * 100);
        int progressLimit = (int) ((currentLimit / currentLimit) * 100);

        currentSavingBar.setProgress(progressSaving);
        currentLimitBar.setProgress(progressLimit);
    }
}
