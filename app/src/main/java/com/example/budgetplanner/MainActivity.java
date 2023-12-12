package com.example.budgetplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetplanner.database.BudgetingException;
import com.example.budgetplanner.database.DataSource;
import com.example.budgetplanner.database.Utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MonthAdapter adapter;
    private List<MonthItem> monthList;
    private TextView balanceView; // Add TextView for balance
    DataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String month = today.getMonth().toString();
        int year = today.getYear();

        MonthItem monthItem = new MonthItem(month, year);

        try {
            ds = new DataSource(this, monthItem);
        } catch (BudgetingException e) {
            Utils.diagnoseException(MainActivity.this, e);
        }

        // Initialize balanceView
        balanceView = findViewById(R.id.balanceView);

        recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize your data source with the current month and year
        monthList = new ArrayList<>();
        updateMonthList();

        adapter = new MonthAdapter(monthList);
        recyclerView.setAdapter(adapter);

        // Set an item click listener for the adapter
        MonthAdapter.OnItemClickListener itemClickListener =
                new MonthAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String selectedMonth, int selectedYear) {
                        // Launch MonthlyStatementInformationActivity with selected month and year
                        Intent intent = new Intent(MainActivity.this, MonthlyStatementInformationActivity.class);
                        intent.putExtra("month", selectedMonth);
                        intent.putExtra("year", selectedYear);
                        startActivity(intent);
                    }
                };

        adapter.setOnItemClickListener(itemClickListener);

        // Check for month change and update RecyclerView
        checkForMonthChange();

        // Check if the app has been opened before
        SharedPreferences preferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        boolean isInitialSetupCompleted = preferences.getBoolean("isInitialSetupCompleted", false);

        if (!isInitialSetupCompleted) {
            // If the initial setup is not completed, launch InitialSetupActivity
            Intent intent = new Intent(this, InitialSetupActivity.class);
            startActivity(intent);
        }

        // Set a click listener for the "Exit" button
        Button exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finish the activity and exit the app
            }
        });
    }

    private void updateMonthList() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Clear the list
        monthList.clear();

        // Fetch balance from the database
        double balance = ds.getBalance();

                // Add the current month and year with the fetched balance
                monthList.add(new MonthItem(getMonthName(currentMonth), currentYear));

        // Update balanceView
        balanceView.setText(String.format("$%.2f", balance));

        // Notify the adapter that the data has changed
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    private String getMonthName(int month) {
        String[] monthNames = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        };
        return monthNames[month];
    }

    private void checkForMonthChange() {
        final Handler handler = new Handler();
        final int oneMinute = 60 * 1000; // Delay in milliseconds

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);
                String currentMonthName = getMonthName(currentMonth);

                // Check if either month or year has changed
                if (!currentMonthName.equals(monthList.get(0).getMonthName()) || currentYear != monthList.get(0).getYear()) {
                    monthList.add(0, new MonthItem(currentMonthName, currentYear));
                    adapter.notifyItemInserted(0);
                }

                handler.postDelayed(this, oneMinute);
            }
        }, oneMinute);
    }
}
