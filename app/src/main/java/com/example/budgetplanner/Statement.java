package com.example.budgetplanner;

import android.content.ContentValues;

import static com.example.budgetplanner.BudgetContract.BudgetStatement;

/**
 * Statement class represents one statement in a database table storing statements.
 *
 * @author Avyuktkrishna Ramasamy
 * @version 1.0
 * @since 11/5/23
 */
public class Statement {

    private Long id;
    private String date;
    private String label;
    private Double amount;
    private Integer frequency;
    private String notes;

    public Statement() {

        this(0L, null, null, 0.00, 0, null);
    }

    public Statement(String date, String label, Double amount, Integer frequency, String notes) {

        setId(0L);
        setDate(date);
        setLabel(label);
        setAmount(amount);
        setFrequency(frequency);
        setNotes(notes);
    }

    public Statement(Long id, String date, String label, Double amount, Integer frequency, String notes) {

        setId(id);
        setDate(date);
        setLabel(label);
        setAmount(amount);
        setFrequency(frequency);
        setNotes(notes);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ContentValues toContentValues() {

        ContentValues stmt = new ContentValues();

        stmt.put(BudgetStatement._ID, getId());
        stmt.put(BudgetStatement.COLUMN_NAME_DATE, getDate());
        stmt.put(BudgetStatement.COLUMN_NAME_LABEL, getLabel());
        stmt.put(BudgetStatement.COLUMN_NAME_AMOUNT, getAmount());
        stmt.put(BudgetStatement.COLUMN_NAME_FREQUENCY, getFrequency());
        stmt.put(BudgetStatement.COLUMN_NAME_NOTES, getNotes());

        return stmt;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}
