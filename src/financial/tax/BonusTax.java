package financial.tax;

import java.math.BigDecimal;

public class BonusTax implements TaxStrategy{

    public BigDecimal calculateTax(BigDecimal amount) // 接受参数防止以后修改
    {
        return BigDecimal.valueOf(0.0);
    }
}
