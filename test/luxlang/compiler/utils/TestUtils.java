package luxlang.compiler.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {
    private static final String TEST_DIR = "test";
    private static final String RESOURCES_DIR = "resources";
    private static final String LEXER_DIR = "lexer";

    /**
     * Reads the contents of a test input file from the test resources directory.
     *
     * @param fileName the name of the file to read
     * @return the contents of the file as a string
     * @throws IOException if the file cannot be read
     */
    public static String readTestFile(String fileName) throws IOException {
        // Try relative to current working directory first
        Path filePath = Paths.get(TEST_DIR, RESOURCES_DIR, LEXER_DIR, fileName);
        if (!Files.exists(filePath)) {
            // Try from user.dir system property
            String userDir = System.getProperty("user.dir");
            filePath = Paths.get(userDir, TEST_DIR, RESOURCES_DIR, LEXER_DIR, fileName);
            if (!Files.exists(filePath)) {
                throw new NoSuchFileException("Test file not found: " + fileName);
            }
        }
        return Files.readString(filePath);
    }
}
