package financial.tax;

import java.math.BigDecimal;

public class AllowanceTax implements TaxStrategy{

    @Override
    public BigDecimal calculateTax(BigDecimal amount) {

        return amount.multiply(BigDecimal.valueOf(0.2));
    }
}
