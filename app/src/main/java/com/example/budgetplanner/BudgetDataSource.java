package com.example.budgetplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * DataSource class is used as a layer between backend Java and the SQLite database.
 * Can use this class to add/remove a statement from the database.
 *
 * @author Avyuktkrishna Ramasamy
 * @version 1.0
 * @since 11/2/23
 */
public class BudgetDataSource {

    // Variable(s)
    private SQLiteDatabase db;
    private BudgetDBHelper helper;

    public BudgetDataSource(Context context, String monthTableName, String savingsTableName) {

        this.helper = new BudgetDBHelper(context, monthTableName, savingsTableName);
        this.db = helper.getWritableDatabase();
    }

    public void addStatement(String date, String label, Double amount, String notes) {

        ContentValues statement = new ContentValues();

        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_DATE, date);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_LABEL, label);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_AMOUNT, amount);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_NOTES, notes);

        this.db.insert(helper.getMonthTableName(), null, statement);
    }

    public void removeStatement(String date, String label, Double amount, String notes) {

        db.delete(helper.getMonthTableName(),
                BudgetContract.BudgetStatement.COLUMN_NAME_DATE + "='" + date + "' AND " +
                        BudgetContract.BudgetStatement.COLUMN_NAME_LABEL + "='" + label + "' AND " +
                        BudgetContract.BudgetStatement.COLUMN_NAME_AMOUNT + "='" + amount + "'" +
                        BudgetContract.BudgetStatement.COLUMN_NAME_NOTES + "='" + notes + "'",
                null);
    }

    public void addBudgeting(Double currBalance, Double setLimit, Double savings) {

        ContentValues statement = new ContentValues();

        statement.put(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE, currBalance);
        statement.put(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT, setLimit);
        statement.put(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS, savings);

        this.db.insert(helper.getSavingsTableName(), null, statement);
    }

    public void close() {

        db.close();
        helper.close();
    }
}
