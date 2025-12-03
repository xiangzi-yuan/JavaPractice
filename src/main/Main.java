package main;

import financial.expense.ExpenseRecord;
import financial.expense.ExpenseType;
import financial.income.IncomeRecord;
import financial.income.IncomeType;
import financial.ledger.Ledger;
import financial.ledger.LedgerItem;
import financial.util.DateUtils;
import financial.util.IdGenerator;
import financial.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String FILE_NAME = "ledger.csv";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Ledger ledger = new Ledger();

        System.out.println("个人收支与纳税系统（控制台版）");

        // ===== 启动时自动加载 =====
        System.out.println("正在加载历史记录...");
        ledger.load(FILE_NAME);
        System.out.println("加载完毕。输入 help 查看命令列表。");

        while (true) {
            System.out.print("\n> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String cmd = line.split("\\s+")[0].toLowerCase();

            switch (cmd) {
                case "help" -> printHelp();
                case "income" -> handleAddIncome(sc, ledger);
                case "expense" -> handleAddExpense(sc, ledger);
                case "summary" -> handleSummary(sc, ledger);
                case "details" -> handleDetails(sc, ledger);
                case "income-type" -> handleIncomeTypeDetails(sc, ledger);
                case "expense-type" -> handleExpenseTypeDetails(sc, ledger);
                case "exit", "quit" -> {
                    ledger.save(FILE_NAME);
                    System.out.println("系统已退出，数据已保存。");
                    return;
                }
                default -> System.out.println("未知命令: " + cmd + "（输入 help 查看命令）");
            }
        }
    }

    // ===================== 录入收入 =====================
    private static void handleAddIncome(Scanner sc, Ledger ledger) {
        System.out.println("=== 录入收入 ===");

        BigDecimal amount = readAmount(sc);

        String date = readDate(sc);

        System.out.print("备注: ");
        String note = sc.nextLine().trim();

        System.out.println("收入类型：");
        System.out.println(" 0: 工资");
        System.out.println(" 1: 奖金");
        System.out.println(" 2: 津贴");
        System.out.print("编号: ");

        int choice = readInt(sc);
        IncomeType type = switch (choice) {
            case 0 -> IncomeType.SALARY;
            case 1 -> IncomeType.BONUS;
            case 2 -> IncomeType.ALLOWANCE;
            default -> IncomeType.ALLOWANCE;
        };

        String id = IdGenerator.generate(date);
        IncomeRecord record = new IncomeRecord(id, amount, date, note, type);
        ledger.addIncome(record);

        System.out.println("已添加收入（ID=" + id + "）： " + record);
    }

    // ===================== 录入支出 =====================
    private static void handleAddExpense(Scanner sc, Ledger ledger) {
        System.out.println("=== 录入支出 ===");

        BigDecimal amount = readAmount(sc);

        String date = readDate(sc);

        System.out.print("备注: ");
        String note = sc.nextLine().trim();

        System.out.println("支出类别：");
        System.out.println(" 0: 食物");
        System.out.println(" 1: 房租");
        System.out.println(" 2: 交通");
        System.out.println(" 3: 娱乐");
        System.out.println(" 4: 其他");
        System.out.print("编号: ");

        int choice = readInt(sc);
        ExpenseType type = switch (choice) {
            case 0 -> ExpenseType.FOOD;
            case 1 -> ExpenseType.RENT;
            case 2 -> ExpenseType.TRANSPORT;
            case 3 -> ExpenseType.ENTERTAINMENT;
            default -> ExpenseType.OTHER;
        };

        String id = IdGenerator.generate(date);
        ExpenseRecord record = new ExpenseRecord(id, amount, date, note, type);
        ledger.addExpense(record);

        System.out.println("已添加支出（ID=" + id + "）： " + record);
    }

    // ===================== 查看汇总 =====================
    private static void handleSummary(Scanner sc, Ledger ledger) {
        System.out.println("=== 查看汇总 ===");

        String ym = readYearMonth(sc);

        System.out.println("=== " + ym + " 汇总 ===");
        System.out.println("总收入：" + ledger.totalIncomeOfMonth(ym));
        System.out.println("总支出：" + ledger.totalExpenseOfMonth(ym));
        System.out.println("总税额：" + ledger.totalTaxOfMonth(ym));
        System.out.println("净结余：" + ledger.balanceOfMonth(ym));
    }

    // ===================== 查看明细（全部） =====================
    private static void handleDetails(Scanner sc, Ledger ledger) {
        System.out.println("=== 查看当月明细 ===");

        String ym = readYearMonth(sc);

        List<LedgerItem> items = ledger.itemsOfMonth(ym);
        if (items.isEmpty()) {
            System.out.println("该月没有记录。");
            return;
        }

        printItems(items);
    }

    // ===================== 按收入类型查看明细 =====================
    private static void handleIncomeTypeDetails(Scanner sc, Ledger ledger) {
        System.out.println("=== 按收入类型查看明细 ===");

        String ym = readYearMonth(sc);

        System.out.print("请输入类型（工资/奖金/津贴）：");
        String name = sc.nextLine().trim();

        List<LedgerItem> items = ledger.incomeItemsOfType(ym, name);

        if (items.isEmpty()) {
            System.out.println("没有找到记录。");
            return;
        }

        printItems(items);
    }

    // ===================== 按支出类别查看明细 =====================
    private static void handleExpenseTypeDetails(Scanner sc, Ledger ledger) {
        System.out.println("=== 按支出类别查看明细 ===");

        String ym = readYearMonth(sc);

        System.out.print("请输入类别（食物/房租/交通/娱乐/其他）：");
        String name = sc.nextLine().trim();

        List<LedgerItem> items = ledger.expenseItemsOfType(ym, name);

        if (items.isEmpty()) {
            System.out.println("没有找到记录。");
            return;
        }

        printItems(items);
    }

    // ===================== 工具函数区 =====================

    private static BigDecimal readAmount(Scanner sc) {
        while (true) {
            System.out.print("金额: ");
            String s = sc.nextLine().trim();
            try {
                return MoneyUtils.parse(s);
            } catch (Exception e) {
                System.out.println("金额格式错误，请重新输入。");
            }
        }
    }

    private static String readDate(Scanner sc) {
        while (true) {
            System.out.print("日期 (yyyy-MM-dd): ");
            String date = sc.nextLine().trim();
            if (DateUtils.isValidDate(date)) return date;
            System.out.println("日期格式错误，请重新输入。");
        }
    }

    private static String readYearMonth(Scanner sc) {
        while (true) {
            System.out.print("月份 (yyyy-MM): ");
            String ym = sc.nextLine().trim();
            if (DateUtils.isValidYearMonth(ym)) return ym;
            System.out.println("格式错误，请重新输入。");
        }
    }

    private static int readInt(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("请输入数字。");
            }
        }
    }

    private static void printItems(List<LedgerItem> items) {
        System.out.println("ID             日期        类型  类别      金额      税额      备注");
        System.out.println("--------------------------------------------------------------------------");
        for (LedgerItem item : items) {
            System.out.printf(
                    "%s  %s  %s  %s  %s  %s  %s%n",
                    item.id(),
                    item.date(),
                    item.kind(),
                    item.typeOrCategory(),
                    item.amount(),
                    item.tax(),
                    item.note()
            );
        }
    }

    private static void printHelp() {
        System.out.println("可用命令：");
        System.out.println("  help           显示帮助");
        System.out.println("  income         录入收入");
        System.out.println("  expense        录入支出");
        System.out.println("  summary        查看某个月汇总");
        System.out.println("  details        查看某个月全部明细");
        System.out.println("  income-type    按收入类型查看某月明细");
        System.out.println("  expense-type   按支出类别查看某月明细");
        System.out.println("  exit/quit      退出程序");
    }
}
