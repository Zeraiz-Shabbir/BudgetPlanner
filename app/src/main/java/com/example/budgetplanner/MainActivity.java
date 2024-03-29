package com.example.budgetplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MonthAdapter adapter;
    private List<MonthItem> monthList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize your data source with current month and year
        monthList = new ArrayList<>();
        updateMonthList();

        adapter = new MonthAdapter(monthList);
        recyclerView.setAdapter(adapter);

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

        //DBTest.test(MainActivity.this);
    }

    private void updateMonthList() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Clear the list
        monthList.clear();

        // Add the current month and year
        monthList.add(new MonthItem(getMonthName(currentMonth), currentYear));

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
