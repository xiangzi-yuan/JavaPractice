package financial.expense;

import java.math.BigDecimal;

public record ExpenseRecord(
        String id,
        BigDecimal amount,
        String date,        // "2025-03-02"
        String note,
        ExpenseType type
) {
}
