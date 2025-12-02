package financial.tax;

import financial.income.IncomeRecord;
import financial.income.IncomeType;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public final class TaxCalculator{  // 工具类
    private static final Map<IncomeType,TaxStrategy> STRATEGIES = new EnumMap<>(IncomeType.class);

    static {
        STRATEGIES.put(IncomeType.SALARY, new SalaryTax());
        STRATEGIES.put(IncomeType.BONUS, new BonusTax());
        STRATEGIES.put(IncomeType.ALLOWANCE, new AllowanceTax());
    }

    //静态初始化块 / 静态代码块：只能访问 static 的东西（这里就是 STRATEGIES）。会在类第一次被使用时执行一次。
    private TaxCalculator() {} // 工具类，不允许 new
    public static BigDecimal calculateTax(IncomeRecord record)
    {
        TaxStrategy strategy = STRATEGIES.get(record.type());
        if(strategy == null) throw new IllegalStateException("No tax strategy configured for type: " + record.type());
        return strategy.calculateTax(record.amount());

    }

}
