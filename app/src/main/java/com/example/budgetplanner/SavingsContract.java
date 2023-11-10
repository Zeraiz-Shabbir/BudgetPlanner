package com.example.budgetplanner;

import android.provider.BaseColumns;

/**
 * Contract class which explicitly specifies the layout of the schema,
 * a formal declaration of how the SQLite table for budgeting is organized.
 *
 * @author Avyuktkrishna Ramasamy
 * @version 1.0
 * @since 11/4/23
 */
public class SavingsContract {

    // Table name
    private String tableName;

    // Create a new table
    private String sqlCreateStatements;

    // Remove table
    private String sqlDeleteStatements;

    /**
     * Constructor has been made package-level access in order
     * to prevent it from being instantiated outside the package
     */
    SavingsContract(String tableName) {

        setTableName(tableName);
        setSqlCreateStatements("CREATE TABLE " + this.tableName + " (" +
                SavingsContract.SavingsStatement._ID + " INTEGER PRIMARY KEY," +
                SavingsContract.SavingsStatement.COLUMN_NAME_CURR_BALANCE + " REAL," +
                SavingsContract.SavingsStatement.COLUMN_NAME_SET_LIMIT + " REAL," +
                SavingsContract.SavingsStatement.COLUMN_NAME_SAVINGS + " REAL)");
        setSqlDeleteStatements("DROP TABLE IF EXISTS " + this.tableName);
    }

    String getTableName() {

        return this.tableName;
    }

    void setTableName(String tableName) {

        this.tableName = tableName;
    }

    String getSqlCreateStatements() {

        return this.sqlCreateStatements;
    }

    void setSqlCreateStatements(String query) {

        this.sqlCreateStatements = query;
    }

    String getSqlDeleteStatements() {

        return this.sqlDeleteStatements;
    }

    void setSqlDeleteStatements(String query) {

        this.sqlDeleteStatements = query;
    }

    /**
     * This inner class defines table contents for budgeting table
     *
     * @author Avyuktkrishna Ramasamy
     * @version 1.0
     * @since 11/2/23
     */
    public static class SavingsStatement implements BaseColumns {

        // Constant(s)
        public static final String COLUMN_NAME_CURR_BALANCE = "balance";
        public static final String COLUMN_NAME_SET_LIMIT = "set_limit";
        public static final String COLUMN_NAME_SAVINGS = "savings";
    }
}
