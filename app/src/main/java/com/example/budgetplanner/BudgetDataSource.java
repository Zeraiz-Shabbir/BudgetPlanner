package com.example.budgetplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.example.budgetplanner.BudgetContract.BudgetStatement;
import static com.example.budgetplanner.SavingsContract.SavingsStatement;

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

        values.put(BudgetStatement.COLUMN_NAME_DATE, stmt.getDate());
        values.put(BudgetStatement.COLUMN_NAME_LABEL, stmt.getLabel());
        values.put(BudgetStatement.COLUMN_NAME_AMOUNT, stmt.getAmount());
        values.put(BudgetStatement.COLUMN_NAME_FREQUENCY, stmt.getFrequency());
        values.put(BudgetStatement.COLUMN_NAME_NOTES, stmt.getNotes());

        return this.db.insert(this.helper.getMonthTableName(), null, values);
    }

    public void removeStatement(Statement stmt) {

        String whereClause = BudgetStatement._ID + "='" + stmt.getId() + "' AND " +
                BudgetStatement.COLUMN_NAME_DATE + "='" + stmt.getDate() + "' AND " +
                BudgetStatement.COLUMN_NAME_LABEL + "='" + stmt.getLabel() + "' AND " +
                BudgetStatement.COLUMN_NAME_AMOUNT + "='" + stmt.getAmount() + "'" +
                BudgetStatement.COLUMN_NAME_FREQUENCY + "='" + stmt.getFrequency() + "'" +
                BudgetStatement.COLUMN_NAME_NOTES + "='" + stmt.getNotes() + "'";

        this.db.delete(this.helper.getMonthTableName(), whereClause, null);
    }

    public void editStatement(Statement stmt) {

        String whereClause = "_id = '" + stmt.getId() + "'";
        ContentValues values = stmt.toContentValues();

        this.db.update(this.helper.getMonthTableName(), values, whereClause, null);
    }

    public void addBudgeting(Double currBalance, Double setLimit, Double savings, Double amountSpent) {

        ContentValues values = new ContentValues();

        values.put(SavingsStatement.COLUMN_NAME_CURR_BALANCE, currBalance);
        values.put(SavingsStatement.COLUMN_NAME_SET_LIMIT, setLimit);
        values.put(SavingsStatement.COLUMN_NAME_SAVINGS, savings);
        values.put(SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, amountSpent);

        this.db.insert(this.helper.getSavingsTableName(), null, values);
    }

    public void editBudgeting(Double newBalance, Double newSetLimit, Double newSavings, Double amountSpent) {

        ContentValues values = new ContentValues();

        values.put(SavingsStatement.COLUMN_NAME_CURR_BALANCE, newBalance);
        values.put(SavingsStatement.COLUMN_NAME_SET_LIMIT, newSetLimit);
        values.put(SavingsStatement.COLUMN_NAME_SAVINGS, newSavings);
        values.put(SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, amountSpent);

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public void editBalance(Double newBalance) {

        ContentValues values = new ContentValues();

        values.put(SavingsStatement.COLUMN_NAME_CURR_BALANCE, newBalance);
        values.put(SavingsStatement.COLUMN_NAME_SET_LIMIT, getSetLimit());
        values.put(SavingsStatement.COLUMN_NAME_SAVINGS, getSavings());
        values.put(SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, getAmountSpent());

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public void editSetLimit(Double newSetLimit) {

        ContentValues values = new ContentValues();

        values.put(SavingsStatement.COLUMN_NAME_CURR_BALANCE, getBalance());
        values.put(SavingsStatement.COLUMN_NAME_SET_LIMIT, newSetLimit);
        values.put(SavingsStatement.COLUMN_NAME_SAVINGS, getSavings());
        values.put(SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, getAmountSpent());

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public void editSavings(Double newSavings) {

        ContentValues values = new ContentValues();

        values.put(SavingsStatement.COLUMN_NAME_CURR_BALANCE, getBalance());
        values.put(SavingsStatement.COLUMN_NAME_SET_LIMIT, getSetLimit());
        values.put(SavingsStatement.COLUMN_NAME_SAVINGS, newSavings);
        values.put(SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, getAmountSpent());

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public void editAmountSpent(Double newAmountSpent) {

        ContentValues values = new ContentValues();

        values.put(SavingsStatement.COLUMN_NAME_CURR_BALANCE, getBalance());
        values.put(SavingsStatement.COLUMN_NAME_SET_LIMIT, getSetLimit());
        values.put(SavingsStatement.COLUMN_NAME_SAVINGS, getSavings());
        values.put(SavingsStatement.COLUMN_NAME_AMOUNT_SPENT, newAmountSpent);

        this.db.update(this.helper.getSavingsTableName(), values, null, null);
    }

    public List<Statement> getStatements() {

        List<Statement> statements = new LinkedList<>();
        Statement stmt = null;

        String[] columnsToRetrieve = {
                BudgetStatement.COLUMN_NAME_DATE,
                BudgetStatement.COLUMN_NAME_LABEL,
                BudgetStatement.COLUMN_NAME_AMOUNT,
                BudgetStatement.COLUMN_NAME_NOTES
        };

        String sortOrder = BudgetStatement._ID + " DESC";

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
                        cursor.getInt(4), // Frequency
                        cursor.getString(5)  // Notes
                );
                statements.add(stmt);
            }
        }

        return statements;
    }

    public double getBalance() {

        String[] columnsToRetrieve = {SavingsStatement.COLUMN_NAME_CURR_BALANCE};

        try (
                Cursor cursor = this.db.query(
                        this.helper.getSavingsTableName(),
                        columnsToRetrieve,
                        null, null, null, null, null
                )
        )
        {
            while (cursor.moveToNext()) {

                return cursor.getDouble(1);
            }
        }

        return 0.00;
    }

    public double getSetLimit() {

        String[] columnsToRetrieve = {SavingsStatement.COLUMN_NAME_SET_LIMIT};

        try (
                Cursor cursor = this.db.query(
                        this.helper.getSavingsTableName(),
                        columnsToRetrieve,
                        null, null, null, null, null
                )
        )
        {
            while (cursor.moveToNext()) {

                return cursor.getDouble(2);
            }
        }

        return 0.00;
    }

    public double getSavings() {

        String[] columnsToRetrieve = {SavingsStatement.COLUMN_NAME_SAVINGS};

        try (
                Cursor cursor = this.db.query(
                        this.helper.getSavingsTableName(),
                        columnsToRetrieve,
                        null, null, null, null, null
                )
        )
        {
            while (cursor.moveToNext()) {

                return cursor.getDouble(3);
            }
        }

        return 0.00;
    }

    public double getAmountSpent() {

        String[] columnsToRetrieve = {SavingsStatement.COLUMN_NAME_AMOUNT_SPENT};

        try (
                Cursor cursor = this.db.query(
                        this.helper.getSavingsTableName(),
                        columnsToRetrieve,
                        null, null, null, null, null
                )
        )
        {
            while (cursor.moveToNext()) {

                return cursor.getDouble(4);
            }
        }

        return 0.00;
    }

    public void close() {

        db.close();
        this.helper.close();
    }
}
