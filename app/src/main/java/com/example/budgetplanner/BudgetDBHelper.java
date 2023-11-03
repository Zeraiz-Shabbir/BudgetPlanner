package com.example.budgetplanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Helper class which makes interacting with the database easier.
 *
 * @author Avyuktkrishna Ramasamy
 * @version 1.0
 * @since 11/2/23
 */
public class BudgetDBHelper extends SQLiteOpenHelper {

    // Constant(s)
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BudgetPlanner.db";

    // Variable(s)
    private String tableName;
    private BudgetContract contract;

    public BudgetDBHelper(@Nullable Context context, String tableName) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setTableName(tableName);
        setContract(new BudgetContract(tableName));
    }

    /**
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(getContract().getSqlCreateStatements());
    }

    /**
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(getContract().getSqlDeleteStatements());
        onCreate(sqLiteDatabase);
    }

    String getTableName() {

        return this.tableName;
    }

    void setTableName(String tableName) {

        this.tableName = tableName;
    }

    BudgetContract getContract() {

        return this.contract;
    }

    void setContract(BudgetContract contract) {

        this.contract = contract;
    }
}
