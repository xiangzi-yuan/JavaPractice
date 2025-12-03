package financial.ledger;

import java.nio.charset.StandardCharsets;
import financial.expense.ExpenseRecord;
import financial.expense.ExpenseType;
import financial.income.IncomeRecord;
import financial.income.IncomeType;
import financial.tax.TaxCalculator;
import financial.util.FilePath;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ledger {
    private final List<IncomeRecord> incomes = new ArrayList<>();
    private final List<ExpenseRecord> expenses = new ArrayList<>();

    public Ledger addIncome(IncomeRecord income) {
        incomes.add(income);
        return this;
    }

    public Ledger addExpense(ExpenseRecord expense) {
        expenses.add(expense);
        return this;
    }

    // 约定：date 格式为 "yyyy-MM-dd"，yearMonth 格式为 "yyyy-MM"
    // 逻辑：只要 date 以 yearMonth 开头，就认为是同一个月份
    private boolean isSameMonth(String date, String yearMonth) {
        return date != null && yearMonth != null && date.startsWith(yearMonth);
    }
    private boolean isSameType(String typeRecord, String typeInput) {
        return typeRecord != null
                && typeInput != null
                && typeRecord.equalsIgnoreCase(typeInput); // 或 typeInput.equals(typeRecord)
    }


    public BigDecimal totalIncomeOfMonth(String yearMonth) {
        BigDecimal sum = BigDecimal.ZERO;
        for (IncomeRecord income : incomes) {
            if (isSameMonth(income.date(), yearMonth))
                sum = sum.add(income.amount());
        }
        return sum;
    }

    public BigDecimal totalExpenseOfMonth(String yearMonth) {
        BigDecimal sum = BigDecimal.ZERO;
        for (ExpenseRecord expense : expenses) {
            if (isSameMonth(expense.date(), yearMonth))
                sum = sum.add(expense.amount());
        }
        return sum;
    }

    public BigDecimal totalTaxOfMonth(String yearMonth) {
        BigDecimal sum = BigDecimal.ZERO;
        for (IncomeRecord income : incomes) {
            if (isSameMonth(income.date(), yearMonth))
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

    // 某月明细
    public List<LedgerItem> itemsOfMonth(String yearMonth) {
        List<LedgerItem> list = new ArrayList<>();

        for (IncomeRecord income : incomes) {
            if (isSameMonth(income.date(), yearMonth)) {
                BigDecimal tax = TaxCalculator.calculateTax(income);
                LedgerItem item = new LedgerItem(
                        income.id(),
                        income.date(),
                        "收入",
                        income.type().displayName(), // 比如 "工资"
                        income.amount(),
                        tax,
                        income.note()
                );
                list.add(item);
            }
        }

        // 2. 支出 → LedgerItem
        for (ExpenseRecord expense : expenses) {
            if (isSameMonth(expense.date(), yearMonth)) {
                LedgerItem item = new LedgerItem(
                        expense.id(),
                        expense.date(),
                        "支出",
                        expense.type().displayName(), // 比如 "食物"
                        expense.amount(),
                        BigDecimal.ZERO,              // 支出税额为 0
                        expense.note()
                );
                list.add(item);
            }
        }
        list.sort(Comparator.comparing(LedgerItem::date));
        return list;
    }

    // 分类
    // 按月份 + 收入类型中文名（如 "工资"）筛选收入明细
    public List<LedgerItem> incomeItemsOfType(String yearMonth, String typeName) {
        List<LedgerItem> list = new ArrayList<>();
        if (typeName == null) {
            return list;
        }

        for (IncomeRecord income : incomes) {
            if (isSameMonth(income.date(), yearMonth)
                    && typeName.equals(income.type().displayName())) {

                BigDecimal tax = TaxCalculator.calculateTax(income);
                LedgerItem item = new LedgerItem(
                        income.id(),
                        income.date(),
                        "收入",
                        income.type().displayName(), // 比如 "工资"
                        income.amount(),
                        tax,
                        income.note()
                );
                list.add(item);
            }
        }

        // 按日期排序
        list.sort(Comparator.comparing(LedgerItem::date));
        return list;
    }

    // 按月份 + 支出类别中文名（如 "食物"）筛选支出明细
    public List<LedgerItem> expenseItemsOfType(String yearMonth, String typeName) {
        List<LedgerItem> list = new ArrayList<>();
        if (typeName == null) {
            return list;
        }

        for (ExpenseRecord expense : expenses) {
            if (isSameMonth(expense.date(), yearMonth)
                    && typeName.equals(expense.type().displayName())) {

                LedgerItem item = new LedgerItem(
                        expense.id(),
                        expense.date(),
                        "支出",
                        expense.type().displayName(), // 比如 "食物"
                        expense.amount(),
                        BigDecimal.ZERO,              // 支出税额为 0
                        expense.note()
                );
                list.add(item);
            }
        }

        list.sort(Comparator.comparing(LedgerItem::date));
        return list;
    }

    // 保存文件
    public void save(String filename) {
        StringBuilder sb = new StringBuilder();

        // 写收入
        for (IncomeRecord inc : incomes) {
            sb.append(inc.id()).append(",")
                    .append("income,")
                    .append(inc.amount()).append(",")
                    .append(inc.date()).append(",")
                    .append(inc.note()).append(",")
                    .append(inc.type().displayName())
                    .append("\n");
        }

        // 写支出
        for (ExpenseRecord exp : expenses) {
            sb.append(exp.id()).append(",")
                    .append("expense,")
                    .append(exp.amount()).append(",")
                    .append(exp.date()).append(",")
                    .append(exp.note()).append(",")
                    .append(exp.type().displayName())
                    .append("\n");
        }

        try {

            Path file = FilePath.resolve(filename);
            Files.writeString(
                    file,
                    sb.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("写文件失败", e);
        }
    }

    public void load(String filename) {
        incomes.clear();
        expenses.clear();

        Path path = FilePath.resolve(filename);
        if (!Files.exists(path)) return;
        try{
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length != 6) continue;
                String id = parts[0];
                String kind = parts[1];
                BigDecimal amount = new BigDecimal(parts[2]);
                String date = parts[3];
                String note = parts[4];
                String typeName = parts[5];

                if (kind.equals("income")) {
                    IncomeType type = IncomeType.fromDisplayName(typeName);
                    IncomeRecord inc = new IncomeRecord(id, amount, date, note, type);
                    incomes.add(inc);
                } else {
                    ExpenseType type = ExpenseType.fromDisplayName(typeName);
                    ExpenseRecord exp = new ExpenseRecord(id, amount, date, note, type);
                    expenses.add(exp);
                }
            }
        }catch (IOException e){
            throw new RuntimeException("读文件失败", e);
        }
    }



}
