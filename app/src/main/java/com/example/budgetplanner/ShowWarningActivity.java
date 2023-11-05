package com.example.budgetplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ShowWarningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_warning);

        Button showWarningButton = findViewById(R.id.show_warning_button);

        showWarningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the warning dialog using WarningDialogManager
                WarningDialogManager.showLimitExceededDialog(ShowWarningActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle "Yes" button click (e.g., delete entry, update database)
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}