package com.example.budgetplanner;

/// WarningDialogManager.java
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

public class WarningDialogManager {
    public static void showLimitExceededDialog(Context context, DialogInterface.OnClickListener yesClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.limit_exceeded_warning_popup, null);
        builder.setView(dialogView);

        // Set up the "Yes" button click listener
        builder.setPositiveButton("Yes", yesClickListener);

        // Set up the "No" button click listener
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle "No" button click (if needed)
                dialog.dismiss();
            }
        });

        builder.show();
    }
}



