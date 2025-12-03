package financial.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtils {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter YM_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM");

    private DateUtils() {
        // 禁止实例化工具类
    }

    /** 校验 yyyy-MM-dd */
    public static boolean isValidDate(String s) {
        try {
            LocalDate.parse(s, DATE_FMT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /** 校验 yyyy-MM */
    public static boolean isValidYearMonth(String s) {
        try {
            YearMonth.parse(s, YM_FMT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
