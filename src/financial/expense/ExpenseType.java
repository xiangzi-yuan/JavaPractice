package financial.expense;

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
    public String diaplayName()
    {
        return expenseName;
    }
}
