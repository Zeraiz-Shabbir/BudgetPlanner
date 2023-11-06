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

    public long addStatement(String date, String label, Double amount, String notes) {

        ContentValues statement = new ContentValues();

        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_DATE, date);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_LABEL, label);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_AMOUNT, amount);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_NOTES, notes);

        return this.db.insert(helper.getMonthTableName(), null, statement);
    }

    public void removeStatement(Long id, String date, String label, Double amount, String notes) {

        String whereClause = BudgetContract.BudgetStatement._ID + "='" + id + "' AND " +
                BudgetContract.BudgetStatement.COLUMN_NAME_DATE + "='" + date + "' AND " +
                BudgetContract.BudgetStatement.COLUMN_NAME_LABEL + "='" + label + "' AND " +
                BudgetContract.BudgetStatement.COLUMN_NAME_AMOUNT + "='" + amount + "'" +
                BudgetContract.BudgetStatement.COLUMN_NAME_NOTES + "='" + notes + "'";

        this.db.delete(helper.getMonthTableName(), whereClause, null);
    }

    public void editStatement(Long id, String newDate, String newLabel, Double newAmount, String newNotes) {

        String whereClause = "_id = '" + id + "'";
        ContentValues statement = new ContentValues();

        statement.put(BudgetContract.BudgetStatement._ID, id);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_DATE, newDate);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_LABEL, newLabel);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_AMOUNT, newAmount);
        statement.put(BudgetContract.BudgetStatement.COLUMN_NAME_NOTES, newNotes);

        this.db.update(helper.getMonthTableName(), statement, whereClause, null);
    }

    public void addBudgeting(Double currBalance, Double setLimit, Double savings) {

        ContentValues statement = new ContentValues();

        statement.put(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE, currBalance);
        statement.put(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT, setLimit);
        statement.put(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS, savings);

        this.db.insert(helper.getSavingsTableName(), null, statement);
    }

    public void editBudgeting(Double newBalance, Double newSetLimit, Double newSavings) {

        ContentValues statement = new ContentValues();

        statement.put(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE, newBalance);
        statement.put(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT, newSetLimit);
        statement.put(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS, newSavings);

        this.db.update(helper.getSavingsTableName(), statement, null, null);
    }

    public void close() {

        db.close();
        helper.close();
    }
}
