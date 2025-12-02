package financial.ledger;

import financial.expense.ExpenseRecord;
import financial.income.IncomeRecord;
import financial.tax.TaxCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Ledger {
    private final List<IncomeRecord> incomes = new ArrayList<>();
    private final List<ExpenseRecord> expenses = new ArrayList<>();

    public void addIncome(IncomeRecord income)
    {
        incomes.add(income);
    }
    public void addExpense(ExpenseRecord expense)
    {
        expenses.add(expense);
    }

    // 约定：date 格式为 "yyyy-MM-dd"，yearMonth 格式为 "yyyy-MM"
    // 逻辑：只要 date 以 yearMonth 开头，就认为是同一个月份
    private boolean isSameMonth(String date, String yearMonth) {
        return date != null && yearMonth != null && date.startsWith(yearMonth);
    }
    public BigDecimal totalIncomeOfMonth(String yearMonth) {
        BigDecimal sum = BigDecimal.ZERO;
        for(IncomeRecord income:incomes)
        {
            if(isSameMonth(income.date(),yearMonth))
                sum = sum.add(income.amount());
        }
        return sum;
    }

    public BigDecimal totalExpenseOfMonth(String yearMonth) {
        BigDecimal sum = BigDecimal.ZERO;
        for(ExpenseRecord expense:expenses)
        {
            if(isSameMonth(expense.date(),yearMonth))
                sum = sum.add(expense.amount());
        }
        return sum;
    }

    public BigDecimal totalTaxOfMonth(String yearMonth) {
        BigDecimal sum = BigDecimal.ZERO;
        for(IncomeRecord income:incomes)
        {
            if(isSameMonth(income.date(),yearMonth))
                sum = sum.add(TaxCalculator.calculateTax(income));
        }
        return sum;
    }
    public BigDecimal netIncomeOfMonth(String yearMonth) {
        BigDecimal income = totalIncomeOfMonth(yearMonth);
        BigDecimal tax = totalTaxOfMonth(yearMonth);
        return income.subtract(tax);
    }

    public BigDecimal balanceOfMonth(String yearMonth) {
        BigDecimal net = netIncomeOfMonth(yearMonth);
        BigDecimal expense = totalExpenseOfMonth(yearMonth);
        return net.subtract(expense);
    }
// 净结余 = 税后收入 - 支出


}
