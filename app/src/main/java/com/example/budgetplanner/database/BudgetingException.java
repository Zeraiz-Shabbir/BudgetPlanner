package com.example.budgetplanner.database;

public class BudgetingException extends Exception {

    private int exitCode;
    private Statement statement;
    private ExceptionSource source;

    BudgetingException(String message, int exitCode, Statement statement, ExceptionSource source) {
        super(message);
        this.exitCode = exitCode;
        this.statement = statement;
        this.source = source;
    }

    public int getExitCode() {
        return exitCode;
    }

    public Statement getStatement() {
        return statement;
    }

    public ExceptionSource getSource() {
        return source;
    }

    public enum ExceptionSource {
        CALCULATE_BUDGETING, INSERT_RECURRING_STATEMENT, INSERT_STATEMENT, DELETE_RECURRING_STATEMENT, DELETE_STATEMENT, UPDATE_RECURRING_STATEMENT, UPDATE_STATEMENT
    }
}
