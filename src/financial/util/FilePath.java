package financial.util;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FilePath {

    private static final Path DATA_DIR = Path.of("data");

    private FilePath() {

    }

    static {
        try {
            Files.createDirectories(DATA_DIR);
        } catch (Exception e) {
            throw new RuntimeException("无法创建数据目录: " + DATA_DIR, e);
        }
    }

    public static Path resolve(String filename) {
        return DATA_DIR.resolve(filename);
    }
}
