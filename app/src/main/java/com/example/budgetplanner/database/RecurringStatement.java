package com.example.budgetplanner.database;

import androidx.annotation.NonNull;

public final class RecurringStatement extends Statement {

    private int frequencyInDays;

    public RecurringStatement() {
        super();
        this.setFrequencyInDays(0);
    }

    public RecurringStatement(long sid, String date, String label, double amount, String notes, boolean isExpense, int frequencyInDays) {
        super(sid, date, label, amount, notes, isExpense);
        this.setFrequencyInDays(frequencyInDays);
    }

    public void setFrequencyInDays(int frequencyInDays) {
        this.frequencyInDays = frequencyInDays;
    }

    public int getFrequencyInDays() {
        return this.frequencyInDays;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() +
        "RecurringStatement{" +
        "frequencyInDays=" + frequencyInDays +
        '}';
    }
}
