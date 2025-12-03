package financial.income;

import java.math.BigDecimal;

public record IncomeRecord(
        String id,
        BigDecimal amount,
        String date,
        String note,
        IncomeType type
) { }