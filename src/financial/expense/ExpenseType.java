package financial.expense;

import financial.income.IncomeType;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

public enum ExpenseType {
    FOOD("食物"),
    RENT("房租"),
    TRANSPORT("交通"),
    ENTERTAINMENT("娱乐"),
    OTHER("其他");
    private final String expenseName;
    ExpenseType(String expenseName)
    {
        this.expenseName = expenseName;
    }
    public String displayName()
    {
        return expenseName;
    }

    private static final Map<String, ExpenseType> NAME_MAP = new HashMap<>();

    static {
        for(ExpenseType et : values()){
            NAME_MAP.put(et.expenseName, et);
        }
    }
    public static ExpenseType fromDisplayName (String name)
    {
        return NAME_MAP.get(name);
    }
}
