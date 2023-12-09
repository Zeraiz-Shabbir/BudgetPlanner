package com.example.budgetplanner.database;

import static com.example.budgetplanner.database.DatabaseContract.BudgetingEntry;
import static com.example.budgetplanner.database.DatabaseContract.RecurringStatementEntry;
import static com.example.budgetplanner.database.DatabaseContract.StatementEntry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

final class DatabaseManager {

    public static final String DATABASE_NAME = "budgetPlanner.db";
    private DatabaseHelper helper;
    private SQLiteDatabase database;
    private List<Statement> statements;
    private List<RecurringStatement> recurringStatements;
    private Budgeting budgeting;
    private Context context;

    public DatabaseManager(@Nullable Context context, int databaseVersion, @Nullable String recurringStatementTableName, @Nullable String statementTableName) {
        this.helper = new DatabaseHelper(context, DATABASE_NAME, databaseVersion, recurringStatementTableName, statementTableName);
        this.database = this.helper.getWritableDatabase();
        this.statements = new ArrayList<Statement>();
        this.recurringStatements = new ArrayList<RecurringStatement>();
        this.budgeting = new Budgeting();
        this.readStatements();
        this.readRecurringStatements();
        this.readBudgeting();
        //Toast.makeText(context, "balance=" + this.budgeting.getBalance(), Toast.LENGTH_SHORT).show();
    }

    public void save() {
        Log.d("DatabaseManager", "Saving data - Start of transaction");
        this.database.beginTransaction();
        try {
            // Insert budgeting data
            Log.d("DatabaseManager", "Inserting budgeting data");
            this.database.insert(this.helper.getBudgetingTableName(), null, Utils.toContentValues(this.budgeting));

            // Insert recurring statements
            for (RecurringStatement recurringStatement : this.recurringStatements) {
                Log.d("DatabaseManager", "Inserting recurring statement: " + recurringStatement.toString());
                this.database.insert(this.helper.getRecurringStatementTableName(), null, Utils.toContentValues(recurringStatement));
            }

            // Insert regular statements
            for (Statement statement : this.statements) {
                Log.d("DatabaseManager", "Inserting regular statement: " + statement.toString());
                this.database.insert(this.helper.getStatementTableName(), null, Utils.toContentValues(statement));
            }

            // Mark the transaction as successful
            this.database.setTransactionSuccessful();
            Log.d("DatabaseManager", "Transaction marked as successful");
        } finally {
            // End the transaction
            this.database.endTransaction();
            Log.d("DatabaseManager", "End of transaction");
        }
    }

    public void close() {
        this.save();
        this.database.close();
        this.helper.close();
    }

    public void changeTable(String newStatementTableName) {
        this.save();
        this.helper.changeTable(newStatementTableName);
        this.database.close();
        this.database = this.helper.getWritableDatabase();
        this.readStatements();
        this.readRecurringStatements();
        this.readBudgeting();
    }

    private void readStatements() {
        Statement statement = new Statement();
        String[] projection = {
                StatementEntry.COLUMN_SID,
                StatementEntry.COLUMN_DATE,
                StatementEntry.COLUMN_LABEL,
                StatementEntry.COLUMN_AMOUNT,
                StatementEntry.COLUMN_NOTES,
                StatementEntry.COLUMN_EXPENSE
        };
        String sortOrder = StatementEntry.COLUMN_DATE + " DESC";
        try (Cursor cursor = this.database.query(
                this.helper.getStatementTableName(),
                projection,
                null, null, null, null,
                sortOrder
        )) {
            if (cursor.getCount() == 0) {
                return;
            }
            while (cursor.moveToNext()) {
                statement.setStatementID(cursor.getLong(cursor.getColumnIndexOrThrow(projection[0])));
                statement.setDate(cursor.getString(cursor.getColumnIndexOrThrow(projection[1])));
                statement.setLabel(cursor.getString(cursor.getColumnIndexOrThrow(projection[2])));
                statement.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(projection[3])));
                statement.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(projection[4])));
                statement.setExpense(cursor.getInt(cursor.getColumnIndexOrThrow(projection[5])) == 1);
                if (!this.statements.add(statement)) {
                    throw new RuntimeException("Statement " + statement + " was not retrieved due to unknown reasons");
                }
            }
        }
    }

    private void readRecurringStatements() {
        RecurringStatement statement = new RecurringStatement();
        String[] projection = {
                RecurringStatementEntry.COLUMN_SID,
                RecurringStatementEntry.COLUMN_DATE,
                RecurringStatementEntry.COLUMN_LABEL,
                RecurringStatementEntry.COLUMN_AMOUNT,
                RecurringStatementEntry.COLUMN_NOTES,
                RecurringStatementEntry.COLUMN_EXPENSE,
                RecurringStatementEntry.COLUMN_FREQUENCY,
                RecurringStatementEntry.COLUMN_TIME_UNIT
        };
        String sortOrder = RecurringStatementEntry.COLUMN_DATE + " DESC";
        try (Cursor cursor = this.database.query(
                this.helper.getRecurringStatementTableName(),
                projection,
                null, null, null, null,
                sortOrder
        )) {
            if (cursor.getCount() == 0) {
                return;
            }
            while (cursor.moveToNext()) {
                statement.setStatementID(cursor.getLong(cursor.getColumnIndexOrThrow(projection[0])));
                statement.setDate(cursor.getString(cursor.getColumnIndexOrThrow(projection[1])));
                statement.setLabel(cursor.getString(cursor.getColumnIndexOrThrow(projection[2])));
                statement.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(projection[3])));
                statement.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(projection[4])));
                statement.setExpense(cursor.getInt(cursor.getColumnIndexOrThrow(projection[5])) == 1);
                statement.setFrequency(cursor.getInt(cursor.getColumnIndexOrThrow(projection[6])));
                statement.setTimeUnit(cursor.getString(cursor.getColumnIndexOrThrow(projection[6])).charAt(0));
                if (!this.recurringStatements.add(statement)) {
                    throw new RuntimeException("Statement " + statement + " was not retrieved due to unknown reasons");
                }
            }
        }
    }

    private void readBudgeting() {
        String[] projection = {
                BudgetingEntry.COLUMN_BALANCE,
                BudgetingEntry.COLUMN_AMOUNT_SPENT,
                BudgetingEntry.COLUMN_SPENDING_LIMIT,
                BudgetingEntry.COLUMN_SAVING_LIMIT
        };
        try (Cursor cursor = this.database.query(
                this.helper.getBudgetingTableName(),
                projection,
                null, null, null, null, null
        )) {
            if (cursor.getCount() == 0) {
                return;
            }
            while (cursor.moveToNext()) {
                //Toast.makeText(this.context, String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(projection[0]))), Toast.LENGTH_SHORT).show();
                this.budgeting.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow(projection[0])));
                this.budgeting.setAmountSpent(cursor.getDouble(cursor.getColumnIndexOrThrow(projection[1])));
                this.budgeting.setSpendingLimit(cursor.getDouble(cursor.getColumnIndexOrThrow(projection[2])));
                this.budgeting.setSavingLimit(cursor.getDouble(cursor.getColumnIndexOrThrow(projection[3])));
            }
        }
    }

    public boolean insertStatement(Statement statement) {
        return this.statements.add(statement);
    }

    public boolean deleteStatement(Statement statement) {
        return this.statements.remove(statement);
    }

    public boolean updateStatement(Statement newStatement) {
        int index;
        Statement oldStatement = null;
        for (index = 0; index < this.statements.size(); index++) {
            if (this.statements.get(index).getStatementID() == newStatement.getStatementID()) {
                oldStatement = this.statements.get(index);
                break;
            }
        }
        if (oldStatement == null) return true;
        return (this.statements.set(index, newStatement) == oldStatement);
    }

    public boolean insertRecurringStatement(RecurringStatement recurringStatement) {
        return this.recurringStatements.add(recurringStatement);
    }

    public boolean deleteRecurringStatement(RecurringStatement recurringStatement) {
        return this.recurringStatements.remove(recurringStatement);
    }

    public boolean updateRecurringStatement(RecurringStatement newRecurringStatement) {
        int index;
        RecurringStatement oldRecurringStatement = null;
        for (index = 0; index < this.recurringStatements.size(); index++) {
            if (this.recurringStatements.get(index).getStatementID() == newRecurringStatement.getStatementID()) {
                oldRecurringStatement = this.recurringStatements.get(index);
                break;
            }
        }
        if (oldRecurringStatement == null) return true;
        return (this.recurringStatements.set(index, newRecurringStatement) == oldRecurringStatement);
    }

    public double setBalance(double newBalance) {
        double oldBalance = this.budgeting.getBalance();
        this.budgeting.setBalance(newBalance);
        return oldBalance;
    }

    public double setAmountSpent(double newAmountSpent) {
        double oldAmountSpent = this.budgeting.getAmountSpent();
        this.budgeting.setAmountSpent(newAmountSpent);
        return oldAmountSpent;
    }

    public double setSpendingLimit(double newSpendingLimit) {
        double oldSpendingLimit = this.budgeting.getSpendingLimit();
        this.budgeting.setSpendingLimit(newSpendingLimit);
        return oldSpendingLimit;
    }

    public double setSavingLimit(double newSavingLimit) {
        double oldSavingLimit = this.budgeting.getSavingLimit();
        this.budgeting.setSavingLimit(newSavingLimit);
        return oldSavingLimit;
    }

    public double getBalance() {
        return this.budgeting.getBalance();
    }

    public double getAmountSpent() {
        return this.budgeting.getAmountSpent();
    }

    public double getSpendingLimit() {
        return this.budgeting.getSpendingLimit();
    }

    public double getSavingLimit() {
        return this.budgeting.getSavingLimit();
    }

    public Statement getStatement(long sid) {
        Statement statement = null;
        for (int index = 0; index < this.statements.size(); index++) {
            if (this.statements.get(index).getStatementID() == sid) {
                statement = this.statements.get(index);
                break;
            }
        }
        return statement;
    }

    public RecurringStatement getRecurringStatement(long sid) {
        RecurringStatement recurringStatement = null;
        for (int index = 0; index < this.recurringStatements.size(); index++) {
            if (this.recurringStatements.get(index).getStatementID() == sid) {
                recurringStatement = this.recurringStatements.get(index);
                break;
            }
        }
        return recurringStatement;
    }

    public List<Statement> getStatements() {
        return this.statements;
    }

    public List<RecurringStatement> getRecurringStatements() {
        return this.recurringStatements;
    }

    public Budgeting getBudgeting() {
        return this.budgeting;
    }
}
