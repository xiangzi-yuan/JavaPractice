package financial.income;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

public enum IncomeType{
    SALARY("工资"),
    BONUS("奖金"),
    ALLOWANCE("津贴");

    private final String incomeName;
    IncomeType(String incomeName)   // private 可省略 Java 规定 enum 的构造方法默认就是 private 修饰
    {
        this.incomeName = incomeName;
    }
    public String displayName()
    {
        return incomeName;
    }

    private static final Map<String, IncomeType> NAME_MAP = new HashMap<>();

    static {
        for (IncomeType it : values()) {
            NAME_MAP.put(it.incomeName, it);
        }
    }
    public static IncomeType fromDisplayName (String name)
    {
        return NAME_MAP.get(name);
    }
}