package luxlang.compiler.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {
    /**
     * Reads the contents of a test input file from the test resources directory.
     *
     * @param fileName the name of the file to read
     * @return the contents of the file as a string
     * @throws IOException if the file cannot be read
     */
    public static String readTestFile(String fileName) throws IOException {
        // Try relative to current working directory first
        Path filePath = Paths.get("test", "resources", "lexer", fileName);
        if (!Files.exists(filePath)) {
            // Fallback to user.dir system property
            String userDir = System.getProperty("user.dir");
            filePath = Paths.get(userDir, "test", "resources", "lexer", fileName);
        }
        return Files.readString(filePath);
    }
}
