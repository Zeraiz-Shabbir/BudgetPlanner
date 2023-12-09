package com.example.budgetplanner.database;

import androidx.annotation.NonNull;

public final class RecurringStatement extends Statement {

    private int frequency;
    private char timeUnit;


    public RecurringStatement() {
        super();
        this.setFrequency(0);
    }

    public RecurringStatement(long sid, String date, String label, double amount, String notes, boolean isExpense, int frequency, char timeUnit) {
        super(sid, date, label, amount, notes, isExpense);
        this.setFrequency(frequency);
        this.setTimeUnit(timeUnit);
    }

    public void setFrequency(int frequencyInDays) {
        this.frequency = frequencyInDays;
    }

    public int getFrequency() {
        return this.frequency;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() +
        "RecurringStatement{" +
        "frequencyInDays=" + frequency +
        '}';
    }

    public char getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(char timeUnit) {
        this.timeUnit = timeUnit;
    }
}
