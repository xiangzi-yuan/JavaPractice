# 总项目：个人收支与纳税管理系统（Java 控制台版）

## 一、项目概述

做一个**控制台程序**，帮助用户管理自己的：

- 收入（工资、稿费、奖金等）
- 支出（吃饭、房租、购物等）
- 自动用正确税率计算需要缴纳的税
- 输出按月份或按类别的汇总报表

这个项目要把你这一章学的**抽象类 / 接口 / 多态 / record / BigDecimal / 工具类 / 包结构**尽量串起来。

------

## 二、功能需求

### 1. 基础功能

1. 启动程序后提供一个主菜单（循环）：
   - 1）录入收入
   - 2）录入支出
   - 3）查看当月收支明细
   - 4）查看当月收支汇总（总收入、总支出、净收入、总税额）
   - 5）按类别统计（比如：食物合计、房租合计、工资合计等）
   - 0）退出
2. 录入收入时要求输入：
   - 收入类型（工资、稿费、奖金、其他……）
   - 金额（使用 `BigDecimal`）
   - 日期（可以用字符串例如 `2025-03-01`，不用搞得太复杂）
   - 备注（可选）
3. 录入支出时要求输入：
   - 支出类别（饮食、住房、交通、娱乐、其他……）
   - 金额（`BigDecimal`）
   - 日期
   - 备注
4. 查看当月明细：
   - 按时间顺序列出所有收入、支出
   - 每条记录显示：日期、类别/类型、金额、备注、（收入还要显示税额）
5. 查看当月汇总：
   - 总收入
   - 总支出
   - 需要缴纳的总税额
   - 实际到手收入 = 总收入 - 总税额
   - 净结余 = 实际到手收入 - 总支出
6. 按类别统计：
   - 例如：输出“本月饮食支出：xxx”，“本月工资收入：xxx” 等

> 说明：“当月”的定义可以简单点——比如程序启动时让用户输入一个“要查看的月份”，全部以这个月份过滤即可。

------

## 三、业务模型设计（关键类 / 接口 / record）

这里只给你一个推荐结构，不是唯一答案。

### 1. 收入与税

```
// 纳税能力接口
public interface Taxable {
    BigDecimal getTax(); // 返回该收入应缴税额
}

// 抽象收入
public abstract class Income implements Taxable {
    protected final BigDecimal amount; // 金额
    protected final String date;       // 日期: "2025-03-01"
    protected final String note;       // 备注

    protected Income(BigDecimal amount, String date, String note) { ... }

    public BigDecimal getAmount() { ... }

    public String getDate() { ... }

    public String getNote() { ... }

    public abstract String getTypeName(); // "工资", "稿费" 等
}
```

具体收入类型（多态）：

```
public final class SalaryIncome extends Income {
    public SalaryIncome(BigDecimal amount, String date, String note) { ... }

    @Override
    public BigDecimal getTax() {
        // 示例：5000 以下免税，超过部分 20%
    }

    @Override
    public String getTypeName() { return "工资"; }
}

public final class RoyaltyIncome extends Income { ... }   // 稿费
public final class BonusIncome extends Income { ... }     // 奖金
```

### 2. 支出

```
public abstract class Expense {
    protected final BigDecimal amount;
    protected final String date;
    protected final String note;

    protected Expense(BigDecimal amount, String date, String note) { ... }

    public abstract String getCategoryName(); // "饮食", "住房" 等
}

public final class FoodExpense extends Expense { ... }
public final class RentExpense extends Expense { ... }
public final class TransportExpense extends Expense { ... }
```

> 也可以设计 `interface CategoryNamed { String getCategoryName(); }` 来抽象“有类别名的东西”。

### 3. 记录与聚合（可考虑使用 record）

借助 `record` 表示一条“通用收支记录”的视图，例如用于打印：

```
public record LedgerItem(
        String date,
        String kind,           // "收入" / "支出"
        String typeOrCategory, // "工资" / "饮食"
        BigDecimal amount,
        BigDecimal tax,        // 支出可以为 BigDecimal.ZERO
        String note
) {}
```

你可以在统计时，把 `Income` 和 `Expense` 转成 `LedgerItem` 用来统一展示。

### 4. 账本类（核心业务类）

```
public class Ledger {
    private final List<Income> incomes = new ArrayList<>();
    private final List<Expense> expenses = new ArrayList<>();

    public void addIncome(Income income) { ... }
    public void addExpense(Expense expense) { ... }

    // 根据月份过滤，比如 "2025-03"
    public List<Income> getIncomesOfMonth(String yearMonth) { ... }
    public List<Expense> getExpensesOfMonth(String yearMonth) { ... }

    public BigDecimal totalIncomeOfMonth(String yearMonth) { ... }
    public BigDecimal totalTaxOfMonth(String yearMonth) { ... }
    public BigDecimal totalExpenseOfMonth(String yearMonth) { ... }

    // 按类型 / 类别分组统计，可以用 Map<String, BigDecimal>
    public Map<String, BigDecimal> groupIncomeByType(String yearMonth) { ... }
    public Map<String, BigDecimal> groupExpenseByCategory(String yearMonth) { ... }
}
```

### 5. 菜单与交互层

写一个 `Main` 类，负责与用户交互（Scanner 读输入）：

```
public class Main {
    private static final Ledger ledger = new Ledger();

    public static void main(String[] args) {
        // 循环菜单：读取用户选择 → 调用 ledger 的方法
    }
}
```

> 在这个层次你可以使用**匿名类**或者**内部类**做一些小工具，例如输入校验器、命令处理器等（可选加分项）。

------

## 四、技术要求 / 约束（把这一章内容串起来）

在实现过程中，尽量满足以下要求（可以当成 checklist）：

1. **必须使用接口 + 抽象类 + 多态**
   - 至少有一个接口 `Taxable`
   - 至少有一个抽象类 `Income` 或 `Expense`
   - 至少两个不同实现类，通过父类型进行统一处理
2. **金额一律使用 `BigDecimal`**
   - 禁止用 `double` 存金额
   - 计算时控制小数位，如保留 2 位，使用 `setScale` + `RoundingMode`
3. **适当使用 `record`**
   - 比如 `record LedgerItem(...)` 表示只负责承载数据的不可变对象
   - 或者将“按月汇总结果”设计成一个 `record MonthlySummary(...)`
4. **访问控制与包结构**
   - 定义合理的包，例如：
     - `com.yourname.ledger.model`（Income/Expense 等）
     - `com.yourname.ledger.service`（Ledger）
     - `com.yourname.ledger.app`（Main）
   - 非必要不要都写成 `public`，内部实现可以用包权限 / `private`
5. **工具类 / 静态方法**
   - 可以写一个 `MoneyUtils` 工具类，提供：
     - `static BigDecimal parseAmount(String input)`
     - `static BigDecimal zero()` 等
   - 体现静态方法、静态字段的使用
6. **（可选）使用 `SecureRandom` 生成流水号**
   - 为每条记录生成一个“记录 ID”，例如 `20250301-AB12CD`
   - 使用 `SecureRandom` 生成字母 / 数字序列
7. **（可选进阶）持久化**
   - 初版可以只放在内存
   - 进阶可以把数据保存成文本 / CSV / 简单二进制，下一次启动时加载
   - 这里可以顺带练习 `classpath` 与简单文件 IO（等你后面学到再加）

------

## 五、推荐实现步骤

避免一下子上来就全写完，建议分阶段：

1. **第一阶段：模型打底**
   - 先只做收入部分：`Taxable`、`Income`、`SalaryIncome` 等
   - 写一个简单 `main`，手动创建几个收入，计算总收入和总税额
2. **第二阶段：加上支出与账本**
   - 增加 `Expense` 层次结构
   - 写 `Ledger` 管理 `List<Income>` / `List<Expense>`
   - 支持 “当月收入/支出/税额/汇总” 的计算
3. **第三阶段：加上控制台菜单交互**
   - 用 `Scanner` 循环读用户指令
   - 允许动态录入、查询
4. **第四阶段：加上 record / 分组统计 / 格式化输出**
   - 使用 `record` 做输出视图
   - `Map<String, BigDecimal>` 做按类别统计
   - 格式化金额输出（比如对齐、小数位统一）
5. **第五阶段（可选）：增强与重构**
   - 增加更多收入/支出类型
   - 引入内部类 / 匿名类做命令处理
   - 按包划分、适当重构公共逻辑到工具类
