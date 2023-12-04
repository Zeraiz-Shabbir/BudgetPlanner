package com.example.budgetplanner.database;

public class Statement {

    private long sid;
    private String date;
    private String label;
    private double amount;
    private String notes;
    private boolean isExpense;

    public Statement() {
        this(0L, null, null, 0.00, null, false);
    }

    public Statement(long sid, String date, String label, double amount, String notes, boolean isExpense) {
        this.setStatementID(sid);
        this.setDate(date);
        this.setLabel(label);
        this.setAmount(amount);
        this.setNotes(notes);
        this.setExpense(isExpense);
    }

    public void setStatementID(long sid) {
        this.sid = sid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setExpense(boolean isExpense) {
        this.isExpense = isExpense;
    }

    public long getStatementID() {
        return this.sid;
    }

    public String getDate() {
        return this.date;
    }

    public String getLabel() {
        return this.label;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getNotes() {
        return this.notes;
    }

    public boolean isExpense() {
        return this.isExpense;
    }

    @Override
    public String toString() {
        return "Statement{" +
        "sid=" + sid +
        ", date='" + date + '\'' +
        ", label='" + label + '\'' +
        ", amount=" + amount +
        ", notes='" + notes + '\'' +
        ", isExpense=" + isExpense +
        '}';
    }
}
