package com.example.budgetplanner;

import android.content.Context;

final class DBTest {

    public static void test(Context context) {

        String monthTableName = "nov2023";
        String savingsTableName = "nov2023_budgeting";
        BudgetDataSource dataSource = new BudgetDataSource(context, monthTableName, savingsTableName);

        String date = "11/2/23";
        String label = "EXAMPLE TRANSACTION";
        Double amount = 5000.00;
        String notes = "This is an example transaction.";

        long id = dataSource.addStatement(date, label, amount, notes);

        Double currBalance = 4000.00;
        Double setLimit = 1000.00;
        Double savings = 200.00;

        dataSource.addBudgeting(currBalance, setLimit, savings);

        date = "11/5/23";
        amount = -2000.00;

        dataSource.editStatement(id, date, label, amount, notes);

        dataSource.close();
    }
}
