package luxlang.compiler.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {
    private static final String TEST_RESOURCES_DIR = "test/resources/lexer/";

    /**
     * Reads the contents of a test input file from the test resources directory.
     *
     * @param fileName the name of the file to read
     * @return the contents of the file as a string
     * @throws IOException if the file cannot be read
     */
    public static String readTestFile(String fileName) throws IOException {
        Path filePath = Path.of(TEST_RESOURCES_DIR + fileName);
        return Files.readString(filePath);
    }
}
