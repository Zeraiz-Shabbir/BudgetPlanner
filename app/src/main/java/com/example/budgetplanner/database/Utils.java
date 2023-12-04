package com.example.budgetplanner.database;

import static com.example.budgetplanner.database.DatabaseContract.BudgetingEntry;
import static com.example.budgetplanner.database.DatabaseContract.RecurringStatementEntry;
import static com.example.budgetplanner.database.DatabaseContract.StatementEntry;

import android.content.ContentValues;

public final class Utils {

    private Utils() {}

    public static ContentValues toContentValues(Statement statement) {
        ContentValues values = new ContentValues();
        values.put(StatementEntry.COLUMN_SID, statement.getStatementID());
        values.put(StatementEntry.COLUMN_DATE, statement.getDate());
        values.put(StatementEntry.COLUMN_LABEL, statement.getLabel());
        values.put(StatementEntry.COLUMN_AMOUNT, statement.getAmount());
        values.put(StatementEntry.COLUMN_NOTES, statement.getNotes());
        values.put(StatementEntry.COLUMN_EXPENSE, statement.isExpense());
        return values;
    }

    public static ContentValues toContentValues(RecurringStatement statement) {
        ContentValues values = Utils.toContentValues((Statement) statement);
        values.put(RecurringStatementEntry.COLUMN_FREQUENCY, statement.getFrequencyInDays());
        return values;
    }

    public static ContentValues toContentValues(Budgeting budgeting) {
        ContentValues values = new ContentValues();
        values.put(BudgetingEntry.COLUMN_BALANCE, budgeting.getBalance());
        values.put(BudgetingEntry.COLUMN_AMOUNT_SPENT, budgeting.getAmountSpent());
        values.put(BudgetingEntry.COLUMN_SPENDING_LIMIT, budgeting.getSpendingLimit());
        values.put(BudgetingEntry.COLUMN_SAVING_LIMIT, budgeting.getSavingLimit());
        return values;
    }

    public static Statement toStatement(ContentValues values) {
        long sid = (long) values.get(StatementEntry.COLUMN_SID);
        String date = (String) values.get(StatementEntry.COLUMN_DATE);
        String label = (String) values.get(StatementEntry.COLUMN_LABEL);
        double amount = (double) values.get(StatementEntry.COLUMN_DATE);
        String notes = (String) values.get(StatementEntry.COLUMN_NOTES);
        boolean isExpense = (boolean) values.get(StatementEntry.COLUMN_EXPENSE);
        return new Statement(sid, date, label, amount, notes, isExpense);
    }

    public static Budgeting toBudgeting(ContentValues values) {
        double balance = (double) values.get(BudgetingEntry.COLUMN_BALANCE);
        double amountSpent = (double) values.get(BudgetingEntry.COLUMN_AMOUNT_SPENT);
        double spendingLimit = (double) values.get(BudgetingEntry.COLUMN_SPENDING_LIMIT);
        double savingLimit = (double) values.get(BudgetingEntry.COLUMN_SAVING_LIMIT);
        return new Budgeting(balance, amountSpent, spendingLimit, savingLimit);
    }

    public static RecurringStatement toRecurringStatement(ContentValues values) {
        RecurringStatement statement = (RecurringStatement) Utils.toStatement(values);
        statement.setFrequencyInDays((Integer) values.get(RecurringStatementEntry.COLUMN_FREQUENCY));
        return statement;
    }
}
