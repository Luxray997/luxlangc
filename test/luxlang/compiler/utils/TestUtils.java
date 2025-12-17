package luxlang.compiler.utils;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;
import luxlang.compiler.parser.objects.SourceInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {
    private static final Path RESOURCES_PATH = Paths.get("test", "resources");

    public static String readTestFile(String subdirectory, String fileName) throws IOException {
        Path file =  RESOURCES_PATH.resolve(subdirectory, fileName);
        if (!Files.exists(file)) {
            throw new NoSuchFileException("Test file not found: " + subdirectory + "/" + fileName);
        }
        return Files.readString(file);
    }

    public static SourceInfo dummySourceInfo() {
        Token dummyToken = new Token(TokenKind.EOF, null, 1, 1);
        return new SourceInfo(dummyToken, dummyToken);
    }

}
