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
 * @version 2.0
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

        ContentValues values = stmt.toContentValues();

        values.put(BudgetStatement.COLUMN_NAME_DATE, stmt.getDate());
        values.put(BudgetStatement.COLUMN_NAME_LABEL, stmt.getLabel());
        values.put(BudgetStatement.COLUMN_NAME_AMOUNT, stmt.getAmount());
        values.put(BudgetStatement.COLUMN_NAME_NOTES, stmt.getNotes());

        return this.db.insert(this.helper.getMonthTableName(), null, values);
    }

    public void removeStatement(Statement stmt) {

        String whereClause = BudgetStatement._ID + "='" + stmt.getId() + "' AND " +
                BudgetStatement.COLUMN_NAME_DATE + "='" + stmt.getDate() + "' AND " +
                BudgetStatement.COLUMN_NAME_LABEL + "='" + stmt.getLabel() + "' AND " +
                BudgetStatement.COLUMN_NAME_AMOUNT + "='" + stmt.getAmount() + "'" +
                BudgetStatement.COLUMN_NAME_NOTES + "='" + stmt.getNotes() + "'";

        this.db.delete(this.helper.getMonthTableName(), whereClause, null);
    }

    public void editStatement(Statement stmt) {

        String whereClause = "_id = '" + stmt.getId() + "'";
        ContentValues values = new ContentValues();

        values.put(BudgetStatement._ID, stmt.getId());
        values.put(BudgetStatement.COLUMN_NAME_DATE, stmt.getDate());
        values.put(BudgetStatement.COLUMN_NAME_LABEL, stmt.getLabel());
        values.put(BudgetStatement.COLUMN_NAME_AMOUNT, stmt.getAmount());
        values.put(BudgetStatement.COLUMN_NAME_NOTES, stmt.getNotes());

        this.db.update(this.helper.getMonthTableName(), values, whereClause, null);
    }

    public void addBudgeting(Double currBalance, Double setLimit, Double savings) {

        ContentValues statement = new ContentValues();

        statement.put(SavingsStatement.COLUMN_NAME_CURR_BALANCE, currBalance);
        statement.put(SavingsStatement.COLUMN_NAME_SET_LIMIT, setLimit);
        statement.put(SavingsStatement.COLUMN_NAME_SAVINGS, savings);

        this.db.insert(this.helper.getSavingsTableName(), null, statement);
    }

    public void editBudgeting(Double newBalance, Double newSetLimit, Double newSavings) {

        ContentValues statement = new ContentValues();

        statement.put(SavingsStatement.COLUMN_NAME_CURR_BALANCE, newBalance);
        statement.put(SavingsStatement.COLUMN_NAME_SET_LIMIT, newSetLimit);
        statement.put(SavingsStatement.COLUMN_NAME_SAVINGS, newSavings);

        this.db.update(this.helper.getSavingsTableName(), statement, null, null);
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
                        cursor.getString(4)  // Notes
                );
                statements.add(stmt);
            }
        }

        return statements;
    }

    public void close() {

        db.close();
        this.helper.close();
    }
}
