package financial.util;

import java.security.SecureRandom;

public class IdGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private IdGenerator() {

    }
    private static String randomPart(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
        {
            int index = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }

    public static String generate(String date) {
        // date 格式为 yyyy-MM-dd → 转成 yyyyMMdd
        String day = date.replace("-", "");
        return day + "-" + randomPart(6); // 例如：20250305-AB12CD
    }
}
