package financial.income;

import java.math.BigDecimal;

public record IncomeRecord( BigDecimal amount,
                            String date,
                            String note,
                            IncomeType type)
{

}