package com.example.budgetplanner;

import android.provider.BaseColumns;

/**
 * Contract class which explicitly specifies the layout of the schema,
 * a formal declaration of how the SQLite database is organized.
 *
 * @author Avyuktkrishna Ramasamy
 * @version 2.0
 * @since 11/2/23
 */
public final class BudgetContract {

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
    BudgetContract(String tableName) {

        setTableName(tableName);
        setSqlCreateStatements("CREATE TABLE " + this.tableName + " (" +
                BudgetStatement._ID + " INTEGER PRIMARY KEY," +
                BudgetStatement.COLUMN_NAME_DATE + " TEXT," +
                BudgetStatement.COLUMN_NAME_LABEL + " TEXT," +
                BudgetStatement.COLUMN_NAME_AMOUNT + " REAL," +
                BudgetStatement.COLUMN_NAME_NOTES + " TEXT)");
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
     * This inner class defines table contents for each monthly statement information table
     *
     * @author Avyuktkrishna Ramasamy
     * @version 1.0
     * @since 11/2/23
     */
    public static class BudgetStatement implements BaseColumns {

        // Constant(s)
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_NOTES = "notes";
    }
}
