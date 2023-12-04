package com.example.budgetplanner;

import java.time.Month;

public class MonthItem {

    private String month;
    private int year;

    public MonthItem(String month, int year) {
        this.setMonth(month);
        this.setYear(year);
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return this.month;
    }

    public int getYear() {
        return this.year;
    }

    public int getMonthValue() {
        return Month.valueOf(this.month).getValue();
    }

    @Override
    public String toString() {
        return (this.getMonth() + this.getYear());
    }
}