package com.example.budgetplanner;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.time.LocalDate;
import java.time.ZoneId;
import android.widget.Button;

public class BudgetingActivity extends AppCompatActivity {

    private String monthTableName;
    private String savingsTableName;
    private BudgetDataSource ds;
    private ProgressBar currentSavingBar;
    private ProgressBar currentLimitBar;

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
