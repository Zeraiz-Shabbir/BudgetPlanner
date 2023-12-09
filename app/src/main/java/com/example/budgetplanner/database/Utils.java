package com.example.budgetplanner.database;

import static com.example.budgetplanner.database.DatabaseContract.BudgetingEntry;
import static com.example.budgetplanner.database.DatabaseContract.RecurringStatementEntry;
import static com.example.budgetplanner.database.DatabaseContract.StatementEntry;

import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

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
        values.put(RecurringStatementEntry.COLUMN_FREQUENCY, statement.getFrequency());
        values.put(RecurringStatementEntry.COLUMN_TIME_UNIT, String.valueOf(statement.getTimeUnit()));
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
        statement.setFrequency((Integer) values.get(RecurringStatementEntry.COLUMN_FREQUENCY));
        statement.setTimeUnit((char) values.get(RecurringStatementEntry.COLUMN_TIME_UNIT));
        return statement;
    }

    public static void diagnoseException(Context context, BudgetingException e) {
        String dialogMessage = "";
        final String removeStmt = "Remove this statement?";
        final String insertStmt = "Add this statement back?";
        final String updateStmt = "Edit this statement?";
        switch (e.getExitCode()) {
            case 2:
                dialogMessage = "You exceeded your set limit! ";
                break;
            case 1:
            case 3:
                dialogMessage = "You have depleted your savings! ";
                break;
            default:
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
        }
        switch (e.getSource()) {
            case CALCULATE_BUDGETING:
            case INSERT_STATEMENT:
                dialogMessage += removeStmt;
                break;
            case DELETE_STATEMENT:
                dialogMessage += insertStmt;
                break;
            case UPDATE_STATEMENT:
                dialogMessage += updateStmt;
                break;
            case INSERT_RECURRING_STATEMENT:
                dialogMessage = e.getMessage() + dialogMessage + removeStmt;
                break;
            case DELETE_RECURRING_STATEMENT:
                dialogMessage = e.getMessage() + dialogMessage + insertStmt;
                break;
            case UPDATE_RECURRING_STATEMENT:
                dialogMessage = e.getMessage() + dialogMessage + updateStmt;
                break;
        }

        /*
        switch (returnVal) {
            case -1:
                throw new RuntimeException("ERROR: " + statement + " was not inserted due to unknown causes");
            case 1:
            case 3:
                WarningDialogManager.showSavingDepletedDialog(AddIncomeExpenseActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                        dialog.dismiss();
                    }
                });
                break;
            case 2:
                WarningDialogManager.showLimitExceededDialog(AddIncomeExpenseActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                        dialog.dismiss();
                    }
                });
                break;
            case 0:
                break;
        }
        */
    }
}
