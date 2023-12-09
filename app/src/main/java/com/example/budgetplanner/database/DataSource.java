package com.example.budgetplanner.database;

import android.content.Context;

import androidx.annotation.Nullable;

import com.example.budgetplanner.MonthItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import static com.example.budgetplanner.database.BudgetingException.*;

public final class DataSource {

    public static final int INITIAL_DATABASE_VERSION = 1;
    public static final String RECURRING_STATEMENTS_TABLE_NAME = "recurring_statements";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US);
    private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;
    private DatabaseManager manager;
    private LocalDate today;
    private LocalDate tableMonth;

    public DataSource(@Nullable Context context, MonthItem monthItem) throws BudgetingException {
        this.manager = new DatabaseManager(context, INITIAL_DATABASE_VERSION, RECURRING_STATEMENTS_TABLE_NAME, monthItem.toString());
        this.today = LocalDate.now();
        this.tableMonth = LocalDate.of(monthItem.getYear(), monthItem.getMonthValue(), 1);
        this.populateTable();
        this.calculateBudgeting();
    }

    public Budgeting calculateBudgeting() throws BudgetingException {
        int exitCode = 0;
        LocalDate date = null;
        double balance = 0.00, amountSpent = 0.00;
        for (Statement statement : this.manager.getStatements()) {
            date = LocalDate.parse(statement.getDate(), DATE_FORMATTER);
            if (date.isBefore(this.today)) {
                if (statement.isExpense()) {
                    if (statement.getAmount() > this.getBalance()) {
                        exitCode = exitCode + 1;
                    }
                    if (statement.getAmount() + this.getAmountSpent() > this.getSpendingLimit()) {
                        exitCode = exitCode + 2;
                    }
                    if (exitCode != 0) {
                        throw new BudgetingException("When current balance and amount spent were recalculated, ", exitCode, statement, ExceptionSource.CALCULATE_BUDGETING);
                    }
                }
                balance += (statement.isExpense()) ? -statement.getAmount() : statement.getAmount();
                amountSpent += (statement.isExpense()) ? statement.getAmount() : 0.00;
            }
            else break;
        }
        this.manager.setBalance((balance == 0.00) ? this.getBalance() : balance);
        this.manager.setAmountSpent((amountSpent == 0.00) ? this.getAmountSpent() : amountSpent);
        return this.getBudgeting();
    }

    public void changeTable(MonthItem newMonth) throws BudgetingException {
        this.manager.changeTable(newMonth.toString());
        this.populateTable();
        this.calculateBudgeting();
    }

    public void save() {
        this.manager.save();
    }

    public void close() {
        this.manager.close();
    }

    private void populateTable() throws BudgetingException {
        for (RecurringStatement recurringStatement : this.manager.getRecurringStatements()) {
            this.insertRecurringStatement(recurringStatement);
        }
    }

    public void insertRecurringStatement(RecurringStatement recurringStatement) throws BudgetingException {
        if (!this.manager.getRecurringStatements().contains(recurringStatement)) {
            if (!this.manager.insertRecurringStatement(recurringStatement)) {
                throw new BudgetingException("Recurring statement was not inserted due to unknown causes", -1, recurringStatement, ExceptionSource.INSERT_RECURRING_STATEMENT);
            }
        }
        LocalDate nextDate = LocalDate.parse(recurringStatement.getDate(), DATE_FORMATTER);
        Statement statement = (RecurringStatement) recurringStatement;
        Random rand = new Random();
        boolean suppressWarnings = false;
        while ((nextDate.isAfter(this.tableMonth) || nextDate.isEqual(this.tableMonth)) && ((nextDate.getYear() > this.tableMonth.getYear()) || (nextDate.getMonthValue() >= this.tableMonth.getMonthValue()))) {
            if (nextDate.isAfter(this.today)) {
                suppressWarnings = true;
            }
            statement.setStatementID(rand.nextLong());
            statement.setDate(nextDate.format(DATE_FORMATTER));
            int exitCode = this.insertStatement(statement, suppressWarnings);
            if (!suppressWarnings) {
                switch (exitCode) {
                    case -1:
                        throw new BudgetingException("Statement was not inserted due to unknown causes", exitCode, statement, ExceptionSource.INSERT_RECURRING_STATEMENT);
                    case 0:
                    case -2:
                        break;
                    default:
                        throw new BudgetingException("When insertion of statement was attempted, ", exitCode, statement, ExceptionSource.INSERT_RECURRING_STATEMENT);
                }
            }
            switch (recurringStatement.getTimeUnit()) {
                case 'd':
                    nextDate = nextDate.plusDays(recurringStatement.getFrequency());
                    break;
                case 'w':
                    nextDate = nextDate.plusWeeks(recurringStatement.getFrequency());
                    break;
                case 'm':
                    nextDate = nextDate.plusMonths(recurringStatement.getFrequency());
                    break;
                case 'y':
                    nextDate = nextDate.plusYears(recurringStatement.getFrequency());
                    break;
            }
        }
    }

    public void insertStatement(Statement statement) throws BudgetingException {
        int exitCode = this.insertStatement(statement, false);
        switch (exitCode) {
            case -1:
                throw new BudgetingException("Statement was not inserted due to unknown causes", exitCode, statement, ExceptionSource.INSERT_STATEMENT);
            case 0:
            case -2:
                break;
            default:
                throw new BudgetingException("When insertion of statement was attempted, ", exitCode, statement, ExceptionSource.INSERT_STATEMENT);
        }
    }

    private int insertStatement(Statement statement, boolean suppressWarnings) {
        int exitCode = 0;
        if (this.manager.getStatements().contains(statement)) {
            exitCode = -2;
            return exitCode;
        }
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

    public void deleteRecurringStatement(RecurringStatement recurringStatement) throws BudgetingException {
        if (this.manager.getRecurringStatements().contains(recurringStatement)) {
            if (!this.manager.deleteRecurringStatement(recurringStatement)) {
                throw new BudgetingException("Recurring statement was not deleted due to unknown causes", -1, recurringStatement, ExceptionSource.DELETE_RECURRING_STATEMENT);
            }
        }
        LocalDate nextDate = LocalDate.parse(recurringStatement.getDate(), DATE_FORMATTER);
        Statement matchingStatement = null;
        boolean suppressWarnings = false;
        boolean found = false;
        int index = 0;
        while ((nextDate.isAfter(this.tableMonth) || nextDate.isEqual(this.tableMonth)) && ((nextDate.getYear() > this.tableMonth.getYear()) || (nextDate.getMonthValue() >= this.tableMonth.getMonthValue()))) {
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
                        case -2:
                            throw new BudgetingException("Matching statement not found", exitCode, matchingStatement, ExceptionSource.DELETE_RECURRING_STATEMENT);
                        case -1:
                            throw new BudgetingException("Matching statement was not deleted due to unknown causes", exitCode, matchingStatement, ExceptionSource.DELETE_RECURRING_STATEMENT);
                        case 0:
                            break;
                        default:
                            throw new BudgetingException("When removal of matching statement was attempted, ", exitCode, matchingStatement, ExceptionSource.DELETE_RECURRING_STATEMENT);
                    }
                }
            }
            switch (recurringStatement.getTimeUnit()) {
                case 'd':
                    nextDate = nextDate.plusDays(recurringStatement.getFrequency());
                    break;
                case 'w':
                    nextDate = nextDate.plusWeeks(recurringStatement.getFrequency());
                    break;
                case 'm':
                    nextDate = nextDate.plusMonths(recurringStatement.getFrequency());
                    break;
                case 'y':
                    nextDate = nextDate.plusYears(recurringStatement.getFrequency());
                    break;
            }
            found = false;
        }
    }

    public void deleteStatement(Statement statement) throws BudgetingException {
        int exitCode = this.deleteStatement(statement, false);
        switch (exitCode) {
            case -2:
                throw new BudgetingException("Statement not found", exitCode, statement, ExceptionSource.DELETE_STATEMENT);
            case -1:
                throw new BudgetingException("Statement was not deleted due to unknown causes", exitCode, statement, ExceptionSource.DELETE_STATEMENT);
            case 0:
                break;
            default:
                throw new BudgetingException("When removal of matching statement was attempted, ", exitCode, statement, ExceptionSource.DELETE_STATEMENT);
        }
    }

    private int deleteStatement(Statement statement, boolean suppressWarnings) {
        int exitCode = 0;
        if (!this.manager.getStatements().contains(statement)) {
            exitCode = -2;
            return exitCode;
        }
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

    public void updateRecurringStatement(RecurringStatement newRecurringStatement) throws BudgetingException {
        RecurringStatement oldRecurringStatement = this.manager.getRecurringStatement(newRecurringStatement.getStatementID());
        if (!this.manager.updateRecurringStatement(newRecurringStatement)) {
            throw new BudgetingException("Recurring statement was not deleted due to unknown causes", -1, newRecurringStatement, ExceptionSource.UPDATE_RECURRING_STATEMENT);
        }
        LocalDate nextDate = LocalDate.parse(newRecurringStatement.getDate(), DATE_FORMATTER);
        Statement matchingStatement = null;
        boolean suppressWarnings = false;
        boolean found = false;
        int index = 0;
        while ((nextDate.isAfter(this.tableMonth) || nextDate.isEqual(this.tableMonth)) && ((nextDate.getYear() > this.tableMonth.getYear()) || (nextDate.getMonthValue() >= this.tableMonth.getMonthValue()))) {
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
                        case -2:
                            throw new BudgetingException("Matching statement not found", exitCode, matchingStatement, ExceptionSource.UPDATE_RECURRING_STATEMENT);
                        case -1:
                            throw new BudgetingException("Matching statement was not updated due to unknown causes", exitCode, matchingStatement, ExceptionSource.UPDATE_RECURRING_STATEMENT);
                        case 0:
                            break;
                        default:
                            throw new BudgetingException("When editing of matching statement was attempted, ", exitCode, matchingStatement, ExceptionSource.UPDATE_RECURRING_STATEMENT);
                    }
                }
            }
            switch (newRecurringStatement.getTimeUnit()) {
                case 'd':
                    nextDate = nextDate.plusDays(newRecurringStatement.getFrequency());
                    break;
                case 'w':
                    nextDate = nextDate.plusWeeks(newRecurringStatement.getFrequency());
                    break;
                case 'm':
                    nextDate = nextDate.plusMonths(newRecurringStatement.getFrequency());
                    break;
                case 'y':
                    nextDate = nextDate.plusYears(newRecurringStatement.getFrequency());
                    break;
            }
            found = false;
        }
    }

    public void updateStatement(Statement newStatement) throws BudgetingException {
        int exitCode = this.updateStatement(newStatement, false);
        switch (exitCode) {
            case -2:
                throw new BudgetingException("Matching statement not found", exitCode, newStatement, ExceptionSource.UPDATE_STATEMENT);
            case -1:
                throw new BudgetingException("Matching statement was not updated due to unknown causes", exitCode, newStatement, ExceptionSource.UPDATE_STATEMENT);
            case 0:
                break;
            default:
                throw new BudgetingException("When editing of matching statement was attempted, ", exitCode, newStatement, ExceptionSource.UPDATE_STATEMENT);
        }
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

    public List<Statement> getStatements() {
        return this.manager.getStatements();
    }

    public Budgeting getBudgeting() {
        return this.manager.getBudgeting();
    }
}
