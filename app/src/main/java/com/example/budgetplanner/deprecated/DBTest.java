package com.example.budgetplanner.deprecated;

import android.content.Context;

/**
 * DBTest class is used for database testing purposes only.
 *
 * @author Avyuktkrishna Ramasamy
 * @version 1.0
 * @since 11/5/23
 */
@Deprecated
final class DBTest {

    public static void test(Context context) {

        String monthTableName = "nov2023";
        String savingsTableName = "nov2023_budgeting";
        BudgetDataSource dataSource = new BudgetDataSource(context, monthTableName, savingsTableName);

        String date = "11022023";
        String label = "EXAMPLE TRANSACTION";
        Double amount = 5000.00;
        String notes = "This is an example transaction.";
        Integer frequency = 1;

        Statement stmt = new Statement(date, label, amount, frequency, notes);
        long id = dataSource.addStatement(stmt);
        stmt.setId(id);

        Double currBalance = 4000.00;
        Double setLimit = 1000.00;
        Double savings = 200.00;
        Double amountSpent = 500.00;

        dataSource.addBudgeting(currBalance, setLimit, savings, amountSpent);

        String newDate = "11/5/23";
        Double newAmount = -2000.00;
        stmt.setDate(newDate);
        stmt.setAmount(newAmount);

        dataSource.editStatement(stmt);
        dataSource.close();
    }
}
