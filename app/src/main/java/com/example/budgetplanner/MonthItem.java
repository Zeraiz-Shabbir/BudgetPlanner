package com.example.budgetplanner;

public class MonthItem {
    private String monthName;
    private int year;

    public MonthItem(String monthName, int year) {
        this.monthName = monthName;
        this.year = year;
    }

    public String getMonthName() {
        return monthName;
    }

    public int getYear() {
        return year;
    }
}
