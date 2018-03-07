package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathGenerator {
    private static final String OUTPUT_DIR = "output_data";
    public static File generate (String... fileName) throws IOException {
        Path path = Paths.get(OUTPUT_DIR, fileName);
        Files.createDirectories(path.getParent());
        return path.toFile();
    }
}
