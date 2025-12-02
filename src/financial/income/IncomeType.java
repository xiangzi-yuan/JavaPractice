package financial.income;

import java.security.PrivateKey;

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
}