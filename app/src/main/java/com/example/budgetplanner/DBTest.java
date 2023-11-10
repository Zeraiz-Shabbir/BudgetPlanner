package com.example.budgetplanner;

import android.content.Context;

/**
 * DBTest class is used for database testing purposes only.
 *
 * @author Avyuktkrishna Ramasamy
 * @version 1.0
 * @since 11/5/23
 */
final class DBTest {

    public static void test(Context context) {

        String monthTableName = "nov2023";
        String savingsTableName = "nov2023_budgeting";
        BudgetDataSource dataSource = new BudgetDataSource(context, monthTableName, savingsTableName);

        String date = "11/2/23";
        String label = "EXAMPLE TRANSACTION";
        Double amount = 5000.00;
        String notes = "This is an example transaction.";

        Statement stmt = new Statement(date, label, amount, notes);
        long id = dataSource.addStatement(stmt);
        stmt.setId(id);

        Double currBalance = 4000.00;
        Double setLimit = 1000.00;
        Double savings = 200.00;

        dataSource.addBudgeting(currBalance, setLimit, savings);

        String newDate = "11/5/23";
        Double newAmount = -2000.00;
        stmt.setDate(newDate);
        stmt.setAmount(newAmount);

        dataSource.editStatement(stmt);
        dataSource.close();
    }
}
