package com.example.budgetplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetplanner.database.DataSource;

public class BudgetingActivity extends AppCompatActivity {

    public static final String GET_MONTH_FROM_INTENT = "month";
    private DataSource ds;
    private ProgressBar currentSavingBar;
    private ProgressBar currentLimitBar;
    private TextView outputSavings;
    private TextView outputSetLimit;
    private PopupWindow popupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting);
        Intent intent = getIntent();
        String yearMonth = intent.getStringExtra(GET_MONTH_FROM_INTENT);
        int year = Integer.parseInt(yearMonth.substring(yearMonth.length() - 4, yearMonth.length()));
        String month = yearMonth.substring(0, yearMonth.length() - 4);
        MonthItem monthItem = new MonthItem(month, year);
        //Toast.makeText(BudgetingActivity.this, "yearMonth=" + yearMonth, Toast.LENGTH_SHORT).show();
        //Toast.makeText(BudgetingActivity.this, "year=" + year, Toast.LENGTH_SHORT).show();
        //Toast.makeText(BudgetingActivity.this, "month=" + month, Toast.LENGTH_SHORT).show();
        //Toast.makeText(BudgetingActivity.this, "monthItem=" + monthItem.toString(), Toast.LENGTH_SHORT).show();
        this.ds = new DataSource(BudgetingActivity.this, monthItem);
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
                BudgetingActivity.this.ds.setSavingLimit(Double.parseDouble(amount));
                updateProgressBars();
                printSavings();
                // You can perform further actions with the saving amount
            }
        });
    }

    private void showSetLimitInputDialog() {
        SavingAndSetLimitDialogManager.showSetLimitInputDialog(this, new SavingAndSetLimitDialogManager.OnAmountEnteredListener() {
            @Override
            public void onAmountEntered(String amount) {
                showToast("Set Limit Amount: " + amount);
                BudgetingActivity.this.ds.setSpendingLimit(Double.parseDouble(amount));
                updateProgressBars();
                printSetLimit();
                // You can perform further actions with the set limit amount
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateProgressBarsPROTO() {
        double currentSaving = this.ds.getSavingLimit();
        double currentLimit = this.ds.getSpendingLimit();

        // Calculate progress for the savings bar
        int progressSaving = (int) ((currentSaving / currentLimit) * 100);
        this.currentSavingBar.setProgress(progressSaving);

        // Calculate progress for the limit bar
        int progressLimit = (int) ((currentSaving / currentLimit) * 100);
        this.currentLimitBar.setProgress(progressLimit);
    }

    private void updateProgressBars() {
        double amountSpentProgress = (this.ds.getAmountSpent() / this.ds.getSpendingLimit()) * 100;
        double amountSavedProgress = (this.ds.getBalance() / this.ds.getSavingLimit()) * 100;
        //Toast.makeText(BudgetingActivity.this, "amountSpent=" + this.ds.getAmountSpent(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(BudgetingActivity.this, "spendingLimit=" + this.ds.getSpendingLimit(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(BudgetingActivity.this, "balance=" + this.ds.getBalance(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(BudgetingActivity.this, "savingLimit=" + this.ds.getSavingLimit(), Toast.LENGTH_SHORT).show();
        currentLimitBar.setProgress((int) amountSpentProgress);
        currentSavingBar.setProgress((int) amountSavedProgress);
    }

    private void printSavings() {
        outputSavings = (TextView) findViewById(R.id.savingsOutput);
        outputSavings.setText("$" + String.valueOf(this.ds.getSavingLimit()));
        outputSavings.setVisibility(View.VISIBLE);
    }

    private void printSetLimit() {
        outputSetLimit = (TextView) findViewById(R.id.setLimitOutput);
        outputSetLimit.setText("$" + String.valueOf(this.ds.getSpendingLimit()));
        outputSetLimit.setVisibility(View.VISIBLE);
    }
}
