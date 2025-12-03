package financial.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {

    private MoneyUtils(){

    }
    public static BigDecimal parse(String input) {
        try {
            return new BigDecimal(input).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw new IllegalArgumentException("金额格式错误");
        }
    }
}
