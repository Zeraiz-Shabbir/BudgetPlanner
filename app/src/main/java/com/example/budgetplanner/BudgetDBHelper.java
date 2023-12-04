package com.example.budgetplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Helper class which makes interacting with the database easier.
 *
 * @author Avyuktkrishna Ramasamy
 * @version 2.0
 * @since 11/2/23
 */
@Deprecated
public class BudgetDBHelper extends SQLiteOpenHelper {

    // Constant(s)
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BudgetPlanner.db";

    // Variable(s)
    private BudgetContract budgetContract;
    private SavingsContract savingsContract;

    public BudgetDBHelper(@Nullable Context context, String monthTableName, String savingsTableName) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setBudgetContract(new BudgetContract(monthTableName));
        setSavingsContract(new SavingsContract(savingsTableName));
        setMonthTableName(monthTableName);
        setSavingsTableName(savingsTableName);
    }

    /**
     * @param sqLiteDatabase the database
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(getBudgetContract().getSqlCreateStatements());
        sqLiteDatabase.execSQL(getSavingsContract().getSqlCreateStatements());
    }

    /**
     * @param sqLiteDatabase the database
     * @param i the old version number
     * @param i1 the new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(getBudgetContract().getSqlDeleteStatements());
        sqLiteDatabase.execSQL(getSavingsContract().getSqlDeleteStatements());
        onCreate(sqLiteDatabase);
    }

    String getMonthTableName() {

        return getBudgetContract().getTableName();
    }

    void setMonthTableName(String monthTableName) {

        getBudgetContract().setTableName(monthTableName);
    }

    BudgetContract getBudgetContract() {

        return this.budgetContract;
    }

    void setBudgetContract(BudgetContract budgetContract) {

        this.budgetContract = budgetContract;
    }

    public SavingsContract getSavingsContract() {

        return this.savingsContract;
    }

    public void setSavingsContract(SavingsContract savingsContract) {

        this.savingsContract = savingsContract;
    }

    public String getSavingsTableName() {

        return getSavingsContract().getTableName();
    }

    public void setSavingsTableName(String savingsTableName) {

        getSavingsContract().setTableName(savingsTableName);
    }
}
