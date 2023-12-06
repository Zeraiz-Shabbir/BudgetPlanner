package com.example.budgetplanner.database;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.budgetplanner.MonthItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public final class DataSource {

    public static final int INITIAL_DATABASE_VERSION = 1;
    public static final String RECURRING_STATEMENTS_TABLE_NAME = "recurring_statements";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);
    private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;
    private DatabaseManager manager;
    private final LocalDate today;
    private final Timer timer;
    private LocalDate tableMonth;

    public DataSource(@Nullable Context context, MonthItem monthItem) {
        this.manager = new DatabaseManager(context, INITIAL_DATABASE_VERSION, RECURRING_STATEMENTS_TABLE_NAME, monthItem.toString());
        this.today = LocalDate.now();
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DataSource.this.manager.save();
            }
        }, ONE_DAY_MILLIS);
        this.tableMonth = LocalDate.of(monthItem.getYear(), monthItem.getMonthValue(), 1);
        this.populateTable();
        //this.calculateBudgeting();
    }

    public Budgeting calculateBudgeting() {
        LocalDate date = null;
        double balance = 0.00, amountSpent = 0.00;
        for (Statement statement : this.manager.getStatements()) {
            date = LocalDate.parse(statement.getDate(), FORMATTER);
            if (date.isBefore(this.today)) {
                balance += (statement.isExpense()) ? -statement.getAmount() : statement.getAmount();
                amountSpent += (statement.isExpense()) ? statement.getAmount() : 0.00;
            }
            else break;
        }
        this.manager.setBalance((balance == 0.00) ? this.getBalance() : balance);
        this.manager.setAmountSpent((amountSpent == 0.00) ? this.getAmountSpent() : amountSpent);
        return this.manager.getBudgeting();
    }

    public void changeTable(MonthItem newMonth) {
        this.manager.changeTable(newMonth.toString());
        this.populateTable();
        //this.calculateBudgeting();
    }

    public void save() {
        this.manager.save();
    }

    private void populateTable() {
        for (RecurringStatement recurringStatement : this.manager.getRecurringStatements()) {
            this.insertRecurringStatement(recurringStatement);
        }
    }

    public int insertRecurringStatement(RecurringStatement recurringStatement) {
        if (!this.manager.getRecurringStatements().contains(recurringStatement)) {
            if (!this.manager.insertRecurringStatement(recurringStatement)) {
                throw new RuntimeException("ERROR: " + recurringStatement + " was not inserted due to unknown causes");
            }
        }
        LocalDate nextDate = LocalDate.parse(recurringStatement.getDate(), FORMATTER);
        Statement statement = (Statement) recurringStatement;
        Random rand = new Random();
        boolean suppressWarnings = false;
        while ((nextDate.isAfter(this.tableMonth) || nextDate.isEqual(this.tableMonth)) && (nextDate.getMonthValue() == this.tableMonth.getMonthValue())) {
            if (nextDate.isAfter(this.today)) {
                suppressWarnings = true;
            }
            statement.setDate(nextDate.format(FORMATTER));
            int exitCode = this.insertStatement(statement, suppressWarnings);
            if (!suppressWarnings) {
                switch (exitCode) {
                    case -1:
                        throw new RuntimeException("ERROR: " + statement + " was not inserted due to unknown causes");
                    case 1:
                    case 2:
                    case 3:
                        return exitCode;
                }
            }
            nextDate = nextDate.plusDays(recurringStatement.getFrequencyInDays());
            statement.setStatementID(rand.nextLong());
        }
        return 0;
    }

    public int insertStatement(Statement statement) {
        return this.insertStatement(statement, false);
    }

    private int insertStatement(Statement statement, boolean suppressWarnings) {
        int exitCode = 0;
        if (statement.isExpense()) {
            if (statement.getAmount() > this.getBalance()) {
                exitCode = exitCode + 1;
            }
            if (statement.getAmount() + this.getAmountSpent() > this.getSpendingLimit()) {
                exitCode = exitCode + 2;
            }
            if (exitCode != 0) {
                if (suppressWarnings && !this.manager.insertStatement(statement)) {
                    exitCode = -1;
                }
                return exitCode;
            }
            this.manager.setAmountSpent(this.getAmountSpent() + statement.getAmount());
            this.manager.setBalance(this.getBalance() - statement.getAmount());
        }
        else {
            this.manager.setBalance(this.getBalance() + statement.getAmount());
        }
        if (!this.manager.insertStatement(statement)) {
            exitCode = -1;
        }
        return exitCode;
    }

    public int deleteRecurringStatement(RecurringStatement recurringStatement) {
        if (this.manager.getRecurringStatements().contains(recurringStatement)) {
            if (!this.manager.deleteRecurringStatement(recurringStatement)) {
                throw new RuntimeException("ERROR: " + recurringStatement + " was not deleted due to unknown causes");
            }
        }
        LocalDate nextDate = LocalDate.parse(recurringStatement.getDate(), FORMATTER);
        Statement matchingStatement = null;
        boolean suppressWarnings = false;
        boolean found = false;
        int index = 0;
        while ((nextDate.isAfter(this.tableMonth) || nextDate.isEqual(this.tableMonth)) && (nextDate.getMonthValue() == this.tableMonth.getMonthValue())) {
            if (nextDate.isAfter(this.today)) {
                suppressWarnings = true;
            }
            for (; index < this.manager.getStatements().size(); index++) {
                matchingStatement = this.manager.getStatements().get(index);
                if ((matchingStatement.getLabel().equals(recurringStatement.getLabel())) && (matchingStatement.getAmount() == recurringStatement.getAmount()) && (matchingStatement.isExpense() == recurringStatement.isExpense())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                int exitCode = this.deleteStatement(matchingStatement, suppressWarnings);
                if (!suppressWarnings) {
                    switch (exitCode) {
                        case -1:
                            throw new RuntimeException("ERROR: " + matchingStatement + " was not deleted due to unknown causes");
                        case 1:
                        case 2:
                        case 3:
                            return exitCode;
                    }
                }
            }
            nextDate = nextDate.plusDays(recurringStatement.getFrequencyInDays());
            found = false;
        }
        return 0;
    }

    public int deleteStatement(Statement statement) {
        return this.deleteStatement(statement, false);
    }

    private int deleteStatement(Statement statement, boolean suppressWarnings) {
        int exitCode = 0;
        if (statement.isExpense()) {
            this.manager.setAmountSpent(this.getAmountSpent() - statement.getAmount());
            this.manager.setBalance(this.getBalance() + statement.getAmount());
        }
        else {
            if (statement.getAmount() > this.getBalance()) {
                exitCode = 1;
                if (suppressWarnings && !this.manager.deleteStatement(statement)) {
                    exitCode = -1;
                }
                return exitCode;
            }
            this.manager.setBalance(this.getBalance() - statement.getAmount());
        }
        if (!this.manager.deleteStatement(statement)) {
            exitCode = -1;
        }
        return exitCode;
    }

    public int updateRecurringStatement(RecurringStatement newRecurringStatement) {
        RecurringStatement oldRecurringStatement = this.manager.getRecurringStatement(newRecurringStatement.getStatementID());
        if (!this.manager.updateRecurringStatement(newRecurringStatement)) {
            throw new RuntimeException("ERROR: " + newRecurringStatement + " was not updated due to unknown causes");
        }
        LocalDate nextDate = LocalDate.parse(newRecurringStatement.getDate(), FORMATTER);
        Statement matchingStatement = null;
        boolean suppressWarnings = false;
        boolean found = false;
        int index = 0;
        while ((nextDate.isAfter(this.tableMonth) || nextDate.isEqual(this.tableMonth)) && (nextDate.getMonthValue() == this.tableMonth.getMonthValue())) {
            if (nextDate.isAfter(this.today)) {
                suppressWarnings = true;
            }
            for (; index < this.manager.getStatements().size(); index++) {
                matchingStatement = this.manager.getStatements().get(index);
                if ((matchingStatement.getLabel().equals(oldRecurringStatement.getLabel())) && (matchingStatement.getAmount() == oldRecurringStatement.getAmount()) && (matchingStatement.isExpense() == oldRecurringStatement.isExpense())) {
                    found = true;
                    break;
                }
            }
            if (found) {
                int exitCode = this.updateStatement(matchingStatement, suppressWarnings);
                if (!suppressWarnings) {
                    switch (exitCode) {
                        case -1:
                            throw new RuntimeException("ERROR: " + matchingStatement + " was not updated due to unknown causes");
                        case 1:
                        case 2:
                        case 3:
                            return exitCode;
                    }
                }
            }
            nextDate = nextDate.plusDays(newRecurringStatement.getFrequencyInDays());
            found = false;
        }
        return 0;
    }

    public int updateStatement(Statement newStatement) {
        return this.updateStatement(newStatement, false);
    }

    private int updateStatement(Statement newStatement, boolean suppressWarnings) {
        int exitCode = 0;
        Statement oldStatement = this.manager.getStatement(newStatement.getStatementID());
        if (oldStatement == null) {
            exitCode = -2;
            return exitCode;
        }
        if (oldStatement.isExpense() && newStatement.isExpense()) {
            double currAmountSpent = this.getAmountSpent() - oldStatement.getAmount();
            double currBalance = this.getBalance() + oldStatement.getAmount();
            if (newStatement.getAmount() > currBalance) {
                exitCode = exitCode + 1;
            }
            if (newStatement.getAmount() + currAmountSpent > this.getSpendingLimit()) {
                exitCode = exitCode + 2;
            }
            if (exitCode != 0) {
                return exitCode;
            }
            this.manager.setAmountSpent(currAmountSpent + newStatement.getAmount());
            this.manager.setBalance(currBalance - newStatement.getAmount());
        }
        else if (oldStatement.isExpense()) {
            double currAmountSpent = this.getAmountSpent() - oldStatement.getAmount();
            double currBalance = this.getBalance() + oldStatement.getAmount();
            this.manager.setAmountSpent(currAmountSpent - newStatement.getAmount());
            this.manager.setBalance(currBalance + newStatement.getAmount());
        }
        else if (newStatement.isExpense()) {
            double currBalance = this.getBalance() - oldStatement.getAmount();
            if (newStatement.getAmount() > currBalance) {
                exitCode = exitCode + 1;
            }
            if (newStatement.getAmount() + this.getAmountSpent() > this.getSpendingLimit()) {
                exitCode = exitCode + 2;
            }
            if (exitCode != 0) {
                return exitCode;
            }
            this.manager.setAmountSpent(this.getAmountSpent() + newStatement.getAmount());
            this.manager.setBalance(currBalance - newStatement.getAmount());
        }
        else {
            double newBalance = this.getBalance() - oldStatement.getAmount() + newStatement.getAmount();
            this.manager.setBalance(newBalance);
        }
        if (!this.manager.updateStatement(newStatement)) {
            exitCode = -1;
        }
        return exitCode;
    }

    public double getBalance() {
        return this.manager.getBalance();
    }

    public double getAmountSpent() {
        return this.manager.getAmountSpent();
    }

    public double getSpendingLimit() {
        return this.manager.getSpendingLimit();
    }

    public double getSavingLimit() {
        return this.manager.getSavingLimit();
    }

    public void setBalance(double balance) {
        this.manager.setBalance(balance);
    }

    public void setAmountSpent(double amountSpent) {
        this.manager.setAmountSpent(amountSpent);
    }

    public void setSpendingLimit(double spendingLimit) {
        this.manager.setSpendingLimit(spendingLimit);
    }

    public void setSavingLimit(double savingLimit) {
        this.manager.setSavingLimit(savingLimit);
    }
}
