package com.example.budgetplanner.database;

public final class Budgeting {

    private double balance;
    private double amountSpent;
    private double spendingLimit;
    private double savingLimit;

    public Budgeting() {
        this(0.00, 0.00, 0.00, 0.00);
    }

    public Budgeting(double balance, double amountSpent, double spendingLimit, double savingLimit) {
        this.setBalance(balance);
        this.setAmountSpent(amountSpent);
        this.setSpendingLimit(spendingLimit);
        this.setSavingLimit(savingLimit);
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getAmountSpent() {
        return this.amountSpent;
    }

    public void setAmountSpent(double amountSpent) {
        this.amountSpent = amountSpent;
    }

    public double getSpendingLimit() {
        return this.spendingLimit;
    }

    public void setSpendingLimit(double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public double getSavingLimit() {
        return this.savingLimit;
    }

    public void setSavingLimit(double savingLimit) {
        this.savingLimit = savingLimit;
    }
}
