package main;

import financial.expense.ExpenseRecord;
import financial.expense.ExpenseType;
import financial.income.*;
import financial.tax.TaxCalculator;
import java.math.BigDecimal;


public class Main {
    public static void main(String[] args) {

        // 1. 创建三条收入记录
        IncomeRecord salary = new IncomeRecord(
                new BigDecimal("8000"),
                "2025-03-01",
                "三月工资",
                IncomeType.SALARY
        );

        IncomeRecord bonus = new IncomeRecord(
                new BigDecimal("2000"),
                "2025-03-05",
                "绩效奖金",
                IncomeType.BONUS
        );

        IncomeRecord allowance = new IncomeRecord(
                new BigDecimal("300"),
                "2025-03-10",
                "交通津贴",
                IncomeType.ALLOWANCE
        );

        // 2. 打印它们（使用 record 自动生成的 toString）
        System.out.println("=== 原始记录（toString） ===");
        System.out.println(salary);
        System.out.println(bonus);
        System.out.println(allowance);

        // 3. 演示访问字段
        System.out.println("\n=== 单独访问字段 ===");
        System.out.println("工资金额: " + salary.amount());
        System.out.println("工资日期: " + salary.date());
        System.out.println("工资备注: " + salary.note());
        System.out.println("工资类型中文名: " + salary.type().displayName());

        // 4. 简单汇总（示例）
        BigDecimal sum = salary.amount()
                .add(bonus.amount())
                .add(allowance.amount());

        System.out.println("\n=== 简单汇总 ===");
        System.out.println("总收入：" + sum + " 元");

        BigDecimal salaryTax = TaxCalculator.calculateTax(salary);
        BigDecimal bonusTax = TaxCalculator.calculateTax(bonus);
        BigDecimal allowanceTax = TaxCalculator.calculateTax(allowance);
        System.out.println("工资税收"+salaryTax);
        System.out.println("奖金税收"+bonusTax);
        System.out.println("津贴税收"+allowanceTax);
        sum = sum.subtract(salaryTax).subtract(bonusTax).subtract(allowanceTax);
        System.out.println("总收入(税后)：" + sum + " 元");

        ExpenseRecord food1 = new ExpenseRecord(
                new BigDecimal("12.5"),
                "2025-03-01",
                "猪脚饭",
              ExpenseType.FOOD
        );
        System.out.println(food1);

    }
}
