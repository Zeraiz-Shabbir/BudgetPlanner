package com.example.budgetplanner.database;

import android.provider.BaseColumns;

final class DatabaseContract {

    public static final String BUDGETING_TABLE_NAME_SUFFIX = "_budgeting";

    private DatabaseContract() {}

    public static class StatementEntry implements BaseColumns {

        public static final String COLUMN_SID = "sid";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_EXPENSE = "expense";
    }

    public static class RecurringStatementEntry extends StatementEntry {

        public static final String COLUMN_FREQUENCY = "frequency";
    }

    public static class BudgetingEntry implements BaseColumns {

        public static final String COLUMN_BALANCE = "balance";
        public static final String COLUMN_AMOUNT_SPENT = "amount_spent";
        public static final String COLUMN_SPENDING_LIMIT = "spending_limit";
        public static final String COLUMN_SAVING_LIMIT = "saving_limit";
    }
}
