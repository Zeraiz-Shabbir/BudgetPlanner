package com.example.budgetplanner.database;

import static com.example.budgetplanner.database.DatabaseContract.BUDGETING_TABLE_NAME_SUFFIX;
import static com.example.budgetplanner.database.DatabaseContract.BudgetingEntry;
import static com.example.budgetplanner.database.DatabaseContract.RecurringStatementEntry;
import static com.example.budgetplanner.database.DatabaseContract.StatementEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

final class DatabaseHelper extends SQLiteOpenHelper {

    private String statementTableName;
    private String recurringStatementTableName;
    private String createStatementTableSQL;
    private String createRecurringStatementTableSQL;
    private String createBudgetingTableSQL;
    private String deleteStatementTableSQL;
    private String deleteRecurringStatementTableSQL;
    private String deleteBudgetingTableSQL;

    public DatabaseHelper(@Nullable Context context, @Nullable String databaseName, int databaseVersion, String recurringStatementTableName, String statementTableName) {
        super(context, databaseName, null, databaseVersion);
        this.init(statementTableName, recurringStatementTableName);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(this.getCreateStatementTableSQL());
        database.execSQL(this.getCreateRecurringStatementTableSQL());
        database.execSQL(this.getCreateBudgetingTableSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (newVersion < oldVersion) {
            this.onDowngrade(database, oldVersion, newVersion);
            return;
        }
        database.execSQL(this.getDeleteStatementTableSQL());
        database.execSQL(this.getDeleteRecurringStatementTableSQL());
        database.execSQL(this.getDeleteBudgetingTableSQL());
        database.setVersion(newVersion);
        this.onCreate(database);
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        this.onUpgrade(database, newVersion, oldVersion);
    }

    public void changeTable(String newStatementTableName) {
        this.init(newStatementTableName, this.getRecurringStatementTableName());
    }

    private void init(String statementTableName, String recurringStatementTableName) {
        this.setStatementTableName(statementTableName);
        this.setRecurringStatementTableName(recurringStatementTableName);
        this.setCreateStatementTableSQL(
                "CREATE TABLE " + statementTableName + " (" +
                StatementEntry.COLUMN_SID + " INTEGER," +
                StatementEntry.COLUMN_DATE + " TEXT," +
                StatementEntry.COLUMN_LABEL + " TEXT," +
                StatementEntry.COLUMN_AMOUNT + " REAL," +
                StatementEntry.COLUMN_NOTES + " TEXT," +
                StatementEntry.COLUMN_EXPENSE + " INTEGER)"
        );
        this.setCreateRecurringStatementTableSQL(
                "CREATE TABLE " + recurringStatementTableName + " (" +
                RecurringStatementEntry.COLUMN_SID + " INTEGER," +
                RecurringStatementEntry.COLUMN_DATE + " TEXT," +
                RecurringStatementEntry.COLUMN_LABEL + " TEXT," +
                RecurringStatementEntry.COLUMN_AMOUNT + " REAL," +
                RecurringStatementEntry.COLUMN_NOTES + " TEXT," +
                RecurringStatementEntry.COLUMN_EXPENSE + " INTEGER," +
                RecurringStatementEntry.COLUMN_FREQUENCY + " INTEGER)"
        );
        this.setCreateBudgetingTableSQL(
                "CREATE TABLE " + (statementTableName + BUDGETING_TABLE_NAME_SUFFIX) + " (" +
                BudgetingEntry.COLUMN_BALANCE + " REAL," +
                BudgetingEntry.COLUMN_AMOUNT_SPENT + " REAL," +
                BudgetingEntry.COLUMN_SPENDING_LIMIT + " REAL," +
                BudgetingEntry.COLUMN_SAVING_LIMIT + " REAL)"
        );
        this.setDeleteStatementTableSQL(
                "DROP TABLE IF EXISTS " + statementTableName
        );
        this.setDeleteRecurringStatementTableSQL(
                "DROP TABLE IF EXISTS " + recurringStatementTableName
        );
        this.setDeleteBudgetingTableSQL(
                "DROP TABLE IF EXISTS " + (statementTableName + BUDGETING_TABLE_NAME_SUFFIX)
        );
    }

    public String getStatementTableName() {
        return this.statementTableName;
    }

    public void setStatementTableName(String statementTableName) {
        this.statementTableName = statementTableName;
    }

    public String getRecurringStatementTableName() {
        return this.recurringStatementTableName;
    }

    public void setRecurringStatementTableName(String recurringStatementTableName) {
        this.recurringStatementTableName = recurringStatementTableName;
    }

    public String getBudgetingTableName() {
        return (this.getStatementTableName() + BUDGETING_TABLE_NAME_SUFFIX);
    }

    public String getCreateStatementTableSQL() {
        return this.createStatementTableSQL;
    }

    public void setCreateStatementTableSQL(String createStatementTableSQL) {
        this.createStatementTableSQL = createStatementTableSQL;
    }

    public String getCreateRecurringStatementTableSQL() {
        return this.createRecurringStatementTableSQL;
    }

    public void setCreateRecurringStatementTableSQL(String createRecurringStatementTableSQL) {
        this.createRecurringStatementTableSQL = createRecurringStatementTableSQL;
    }

    public String getCreateBudgetingTableSQL() {
        return this.createBudgetingTableSQL;
    }

    public void setCreateBudgetingTableSQL(String createBudgetingTableSQL) {
        this.createBudgetingTableSQL = createBudgetingTableSQL;
    }

    public String getDeleteStatementTableSQL() {
        return this.deleteStatementTableSQL;
    }

    public void setDeleteStatementTableSQL(String deleteStatementTableSQL) {
        this.deleteStatementTableSQL = deleteStatementTableSQL;
    }

    public String getDeleteRecurringStatementTableSQL() {
        return this.deleteRecurringStatementTableSQL;
    }

    public void setDeleteRecurringStatementTableSQL(String deleteRecurringStatementTableSQL) {
        this.deleteRecurringStatementTableSQL = deleteRecurringStatementTableSQL;
    }

    public String getDeleteBudgetingTableSQL() {
        return this.deleteBudgetingTableSQL;
    }

    public void setDeleteBudgetingTableSQL(String deleteBudgetingTableSQL) {
        this.deleteBudgetingTableSQL = deleteBudgetingTableSQL;
    }
}
