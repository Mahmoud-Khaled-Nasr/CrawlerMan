package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class that manage paths, names, etc. of the used files in the filesystem.
 */
public class PathGenerator {
    private static final String OUTPUT_DIR = "output_data";

    /**
     * Generates a path representing a valid path to the file indicated.
     * @param fileName The name of the file needed, preceded with parent directories if needed
     * @return A valid path to the file
     * @throws IOException If an IO error occurred
     */
    public static Path generate (String... fileName) throws IOException {
        Path path = Paths.get(OUTPUT_DIR, fileName);
        Files.createDirectories(path.getParent());
        return path;
    }



}
