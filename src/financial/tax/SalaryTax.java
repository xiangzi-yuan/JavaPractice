package financial.tax;

import java.math.BigDecimal;

public class SalaryTax implements TaxStrategy{

    public BigDecimal calculateTax(BigDecimal amount) // 接受参数防止以后修改
    {
        BigDecimal threshold = BigDecimal.valueOf(5000);
        if (amount.compareTo(threshold) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal excess = amount.subtract(threshold);
        return excess.multiply(new BigDecimal("0.2"));
    }
}
