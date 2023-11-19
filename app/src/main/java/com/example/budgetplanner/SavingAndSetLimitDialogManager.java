package com.example.budgetplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SavingAndSetLimitDialogManager {

    public interface OnAmountEnteredListener {
        void onAmountEntered(String amount);
    }

    public static void showSavingInputDialog(Context context, final OnAmountEnteredListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_saving_input, null);
        final EditText savingAmountEditText = view.findViewById(R.id.editTextSavingAmount);

        builder.setView(view);
        builder.setTitle("Enter Saving Amount");
        builder.setPositiveButton("OK", (dialog, which) -> {
            String savingAmount = savingAmountEditText.getText().toString();
            listener.onAmountEntered(savingAmount);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showSetLimitInputDialog(Context context, final OnAmountEnteredListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_limit_input, null);
        final EditText setLimitAmountEditText = view.findViewById(R.id.editTextSetLimitAmount);

        builder.setView(view);
        builder.setTitle("Enter Set Limit Amount");
        builder.setPositiveButton("OK", (dialog, which) -> {
            String setLimitAmount = setLimitAmountEditText.getText().toString();
            listener.onAmountEntered(setLimitAmount);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

