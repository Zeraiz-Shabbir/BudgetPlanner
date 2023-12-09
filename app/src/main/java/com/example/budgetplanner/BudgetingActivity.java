package com.example.budgetplanner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetplanner.database.BudgetingException;
import com.example.budgetplanner.database.DataSource;
import com.example.budgetplanner.database.Utils;

public class BudgetingActivity extends AppCompatActivity {

    public static final String GET_MONTH_FROM_INTENT = "month";
    private DataSource ds;
    private ProgressBar currentSavingBar, currentLimitBar;
    private TextView outputPercentSavings, outputPercentSetLimit;
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
        try {
            this.ds = new DataSource(BudgetingActivity.this, monthItem);
        } catch (BudgetingException e) {
            Utils.diagnoseException(this, e);
        }
        Button savingButton = findViewById(R.id.saving_button);
        Button setLimitButton = findViewById(R.id.set_limit_button);
        currentSavingBar = findViewById(R.id.current_saving_bar);
        currentLimitBar = findViewById(R.id.current_limit_bar);
        if (this.ds.getSavingLimit() == 0 && this.ds.getSpendingLimit() == 0) {
            currentLimitBar.setProgress(0);
            currentSavingBar.setProgress(0);
        }
        else {
            this.updateProgressBars();
            this.printSavings();
            this.printSetLimit();
        }

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

        ImageButton infoSavingsButton = findViewById(R.id.info_savings);
        ImageButton infoLimitButton = findViewById(R.id.info_limit);

        infoSavingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoPopup("Savings:\nthe amount you want to set as a goal to save this month");
            }
        });

        infoLimitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoPopup("Set Limit:\nthe maximum amount you want to spend this month");
            }
        });
    }

    private void showSavingInputDialog() {
        SavingAndSetLimitDialogManager.showSavingInputDialog(this, new SavingAndSetLimitDialogManager.OnAmountEnteredListener() {
            @Override
            public void onAmountEntered(String amount) {
                //showToast("Saving Amount: " + amount);
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
                //showToast("Set Limit Amount: " + amount);
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

    public void updateProgressBars() {
        double amountSpentProgress = (this.ds.getAmountSpent() / this.ds.getSpendingLimit()) * 100;
        double amountSavedProgress = (this.ds.getBalance() / this.ds.getSavingLimit()) * 100;
        Toast.makeText(BudgetingActivity.this, "amountSpent=" + this.ds.getAmountSpent(), Toast.LENGTH_SHORT).show();
        Toast.makeText(BudgetingActivity.this, "spendingLimit=" + this.ds.getSpendingLimit(), Toast.LENGTH_SHORT).show();
        Toast.makeText(BudgetingActivity.this, "balance=" + this.ds.getBalance(), Toast.LENGTH_SHORT).show();
        Toast.makeText(BudgetingActivity.this, "savingLimit=" + this.ds.getSavingLimit(), Toast.LENGTH_SHORT).show();
        currentLimitBar.setProgress((int) amountSpentProgress);
        currentSavingBar.setProgress((int) amountSavedProgress);
    }

    public void printSavings() {
        TextView outputSavings = (TextView) findViewById(R.id.savingsOutput);
        outputSavings.setText(String.format("$%.2f", this.ds.getSavingLimit()));

        outputPercentSavings = (TextView) findViewById(R.id.percentSavings);
        double savingsPercentage = ((this.ds.getBalance() / this.ds.getSavingLimit()) * 100);
        outputPercentSavings.setText(String.format("%.2f%%", savingsPercentage));
        textColorChange(savingsPercentage, false);
    }

    public void printSetLimit() {
        TextView outputSetLimit = (TextView) findViewById(R.id.setLimitOutput);
        outputSetLimit.setText(String.format("$%.2f", this.ds.getSpendingLimit()));

        outputPercentSetLimit = (TextView) findViewById(R.id.percentSetLimit);
        double setLimitPercentage = ((this.ds.getAmountSpent() / this.ds.getSpendingLimit()) * 100);
        outputPercentSetLimit.setText(String.format("%.2f%%", setLimitPercentage));
        textColorChange(setLimitPercentage, true);
    }

    private void showInfoPopup(String description) {
        // Close existing popup if open
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }

        // Inflate the popup_layout.xml
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);

        // Set the description
        TextView popupText = popupView.findViewById(R.id.popup_description);
        popupText.setText(description);

        // Create the popup window
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Set a transparent background
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Allow clicks outside the popup to dismiss it
        popupWindow.setOutsideTouchable(true);

        // Show the popup at the center of the screen
        popupWindow.showAtLocation(popupView, android.view.Gravity.CENTER, 0, 0);
    }

    private void textColorChange(double percentage, boolean isSetLimit) {

        if (isSetLimit) {
            if (percentage >= 0.00 && percentage <= 50.99) {
                outputPercentSetLimit.setTextColor(Color.GREEN);
                currentLimitBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 51.00 && percentage <= 75.99) {
                outputPercentSetLimit.setTextColor(Color.parseColor("#FFFDD835"));
                currentLimitBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFFDD835"), PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 76.00 && percentage <= 85.99) {
                outputPercentSetLimit.setTextColor(Color.parseColor("#FFA500"));
                currentLimitBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFA500"), PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 86.00) {
                outputPercentSetLimit.setTextColor(Color.RED);
                currentLimitBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            }
        }

        if (!isSetLimit) {
            if (percentage >= 0.00 && percentage <= 24.99) {
                outputPercentSavings.setTextColor(Color.RED);
                currentSavingBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 25.00 && percentage <= 49.99) {
                outputPercentSavings.setTextColor(Color.parseColor("#FFA500"));
                currentSavingBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFA500"), PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 50.00 && percentage <= 74.99) {
                outputPercentSavings.setTextColor(Color.parseColor("#FFFDD835"));
                currentSavingBar.getProgressDrawable().setColorFilter(Color.parseColor("#FFFDD835"), PorterDuff.Mode.SRC_IN);
            } else if (percentage >= 75.00) {
                outputPercentSavings.setTextColor(Color.GREEN);
                currentSavingBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            }
        }

    }

}
