package com.example.budgetplanner.deprecated;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * DataSource class is used as a layer between backend Java and the SQLite database.
 * Can use this class to add/remove a statement from the database.
 *
 * @author Avyuktkrishna Ramasamy
 * @version 4.0
 * @since 11/2/23
 */

@Deprecated
public class BudgetDataSource {

    // Variable(s)
    private SQLiteDatabase db;
    private BudgetDBHelper helper;

    public BudgetDataSource(Context context, String monthTableName, String savingsTableName) {
        this.helper = new BudgetDBHelper(context, monthTableName, savingsTableName);
        this.db = this.helper.getWritableDatabase();
    }

    public long addStatement(Statement stmt) {
        ContentValues values = new ContentValues();

        values.put(BudgetContract.BudgetStatement.COLUMN_NAME_DATE, stmt.getDate());
        values.put(BudgetContract.BudgetStatement.COLUMN_NAME_LABEL, stmt.getLabel());
        values.put(BudgetContract.BudgetStatement.COLUMN_NAME_AMOUNT, stmt.getAmount());
        values.put(BudgetContract.BudgetStatement.COLUMN_NAME_FREQUENCY, stmt.getFrequency());
        values.put(BudgetContract.BudgetStatement.COLUMN_NAME_NOTES, stmt.getNotes());

        return this.db.insert(this.helper.getMonthTableName(), null, values);
    }

    public void removeStatement(Statement stmt) {
        String whereClause = BudgetContract.BudgetStatement._ID + "='" + stmt.getId() + "' AND " +
                BudgetContract.BudgetStatement.COLUMN_NAME_DATE + "='" + stmt.getDate() + "' AND " +
                BudgetContract.BudgetStatement.COLUMN_NAME_LABEL + "='" + stmt.getLabel() + "' AND " +
                BudgetContract.BudgetStatement.COLUMN_NAME_AMOUNT + "='" + stmt.getAmount() + "'" +
                BudgetContract.BudgetStatement.COLUMN_NAME_FREQUENCY + "='" + stmt.getFrequency() + "'" +
                BudgetContract.BudgetStatement.COLUMN_NAME_NOTES + "='" + stmt.getNotes() + "'";

        this.db.delete(this.helper.getMonthTableName(), whereClause, null);
    }

    public void editStatement(Statement stmt) {
        String whereClause = "_id = '" + stmt.getId() + "'";
        ContentValues values = stmt.toContentValues();

        this.db.update(this.helper.getMonthTableName(), values, whereClause, null);
    }

    public void addBudgeting(Double currBalance, Double setLimit, Double savings, Double amountSpent) {
        ContentValues values = new ContentValues();

        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE, currBalance);
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT, setLimit);
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS, savings);
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, amountSpent);

        this.db.insert(this.helper.getSavingsTableName(), null, values);
    }

    public void editBudgeting(Double newBalance, Double newSetLimit, Double newSavings, Double amountSpent) {
        ContentValues values = new ContentValues();

        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE, newBalance);
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT, newSetLimit);
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS, newSavings);
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, amountSpent);

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public void editBalance(Double newBalance) {
        ContentValues values = new ContentValues();

        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE, newBalance);
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT, getSetLimit());
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS, getSavings());
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, getAmountSpent());

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public void editSetLimit(Double newSetLimit) {
        ContentValues values = new ContentValues();

        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE, getBalance());
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT, newSetLimit);
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS, getSavings());
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, getAmountSpent());

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public void editSavings(Double newSavings) {
        ContentValues values = new ContentValues();

        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE, getBalance());
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT, getSetLimit());
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS, newSavings);
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, getAmountSpent());

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public void editAmountSpent(Double newAmountSpent) {
        ContentValues values = new ContentValues();

        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE, getBalance());
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT, getSetLimit());
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS, getSavings());
        values.put(SavingsContract.SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, newAmountSpent);

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public List<Statement> getStatements() {
        List<Statement> statements = new LinkedList<>();
        Statement stmt = null;

        String[] columnsToRetrieve = {
                BudgetContract.BudgetStatement.COLUMN_NAME_DATE,
                BudgetContract.BudgetStatement.COLUMN_NAME_LABEL,
                BudgetContract.BudgetStatement.COLUMN_NAME_AMOUNT,
                BudgetContract.BudgetStatement.COLUMN_NAME_FREQUENCY,
                BudgetContract.BudgetStatement.COLUMN_NAME_NOTES
        };

        String sortOrder = BudgetContract.BudgetStatement._ID + " DESC";

        try (
                Cursor cursor = this.db.query(
                        this.helper.getMonthTableName(),
                        columnsToRetrieve,
                        null, null, null, null,
                        sortOrder
                )
        )
        {
            while (cursor.moveToNext()) {
                stmt = new Statement(
                        cursor.getLong(0),   // ID
                        cursor.getString(1), // Date
                        cursor.getString(2), // Label
                        cursor.getDouble(3), // Amount
                        cursor.getInt(4),    // Frequency
                        cursor.getString(5)  // Notes
                );
                statements.add(stmt);
            }
        }

        return statements;
    }

    public double getBalance() {
        String[] columnsToRetrieve = {SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE};

        try (
                Cursor cursor = this.db.query(
                        this.helper.getSavingsTableName(),
                        columnsToRetrieve,
                        null, null, null, null, null
                )
        )
        {
            while (cursor.moveToNext()) {
                return cursor.getDouble(cursor.getColumnIndexOrThrow(SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE));
            }
        }

        return 0.00;
    }

    public double getSetLimit() {
        String[] columnsToRetrieve = {SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT};

        try (
                Cursor cursor = this.db.query(
                        this.helper.getSavingsTableName(),
                        columnsToRetrieve,
                        null, null, null, null, null
                )
        )
        {
            while (cursor.moveToNext()) {
                return cursor.getDouble(cursor.getColumnIndexOrThrow(SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT));
            }
        }

        return 0.00;
    }

    public double getSavings() {
        String[] columnsToRetrieve = {SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS};

        try (
                Cursor cursor = this.db.query(
                        this.helper.getSavingsTableName(),
                        columnsToRetrieve,
                        null, null, null, null, null
                )
        )
        {
            while (cursor.moveToNext()) {
                return cursor.getDouble(cursor.getColumnIndexOrThrow(SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS));
            }
        }

        return 0.00;
    }

    public double getAmountSpent() {
        String[] columnsToRetrieve = {SavingsContract.SavingsStatement.COLUMN_NAME_AMOUNT_SPENT};

        try (
                Cursor cursor = this.db.query(
                        this.helper.getSavingsTableName(),
                        columnsToRetrieve,
                        null, null, null, null, null
                )
        )
        {
            while (cursor.moveToNext()) {
                return cursor.getDouble(cursor.getColumnIndexOrThrow(SavingsContract.SavingsStatement.COLUMN_NAME_AMOUNT_SPENT));
            }
        }

        return 0.00;
    }

    public void close() {
        db.close();
        this.helper.close();
    }
}

