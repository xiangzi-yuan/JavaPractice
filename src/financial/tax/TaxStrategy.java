package financial.tax;

import java.math.BigDecimal;

public interface TaxStrategy {
    BigDecimal calculateTax(BigDecimal amount);
}
