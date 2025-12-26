package luxlang.compiler.utils;

import luxlang.compiler.analysis.AnalysisResult;
import luxlang.compiler.analysis.Analyzer;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.ir.IRGenerator;
import luxlang.compiler.ir.objects.IRModule;
import luxlang.compiler.lexer.Lexer;
import luxlang.compiler.lexer.LexingResult;
import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;
import luxlang.compiler.parser.Parser;
import luxlang.compiler.parser.ParsingResult;
import luxlang.compiler.parser.nodes.Program;
import luxlang.compiler.parser.objects.SourceInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TestUtils {
    private static final Path RESOURCES_PATH = Paths.get("test", "resources");
    private static final String ANALYSIS_SUBDIRECTORY = "analysis";
    private static final String IR_SUBDIRECTORY = "ir";

    public static String readTestFile(String subdirectory, String fileName) throws IOException {
        Path file =  RESOURCES_PATH.resolve(subdirectory, fileName);
        if (!Files.exists(file)) {
            throw new NoSuchFileException("Test file not found: " + subdirectory + "/" + fileName);
        }
        return Files.readString(file);
    }

    public static AnalyzedProgram analyzeFile(String fileName) throws IOException {
        String input = readTestFile(ANALYSIS_SUBDIRECTORY, fileName);

        Lexer lexer = new Lexer(input);
        LexingResult lexingResult = lexer.lex();
        assertInstanceOf(LexingResult.Success.class, lexingResult);
        List<Token> tokens = ((LexingResult.Success) lexingResult).tokens();

        Parser parser = new Parser(tokens);
        ParsingResult parsingResult = parser.parse();
        assertInstanceOf(ParsingResult.Success.class, parsingResult);
        Program program = ((ParsingResult.Success) parsingResult).program();

        Analyzer analyzer = new Analyzer(program);
        AnalysisResult result = analyzer.analyze();
        assertInstanceOf(AnalysisResult.Success.class, result);
        return ((AnalysisResult.Success) result).analyzedProgram();
    }

    public static IRModule buildIR(String fileName) throws IOException {
        String input = readTestFile(IR_SUBDIRECTORY, fileName);

        Lexer lexer = new Lexer(input);
        LexingResult lexingResult = lexer.lex();
        assertInstanceOf(LexingResult.Success.class, lexingResult);
        List<Token> tokens = ((LexingResult.Success) lexingResult).tokens();

        Parser parser = new Parser(tokens);
        ParsingResult parsingResult = parser.parse();
        assertInstanceOf(ParsingResult.Success.class, parsingResult);
        Program program = ((ParsingResult.Success) parsingResult).program();

        Analyzer analyzer = new Analyzer(program);
        AnalysisResult analysisResult = analyzer.analyze();
        assertInstanceOf(AnalysisResult.Success.class, analysisResult);
        AnalyzedProgram analyzedProgram = ((AnalysisResult.Success) analysisResult).analyzedProgram();

        IRGenerator builder = new IRGenerator(analyzedProgram);
        return builder.generate();
    }

    public static String normalizeIR(String ir) {
        return ir.lines()
                .map(line -> {
                    int commentIndex = line.indexOf(';');
                    if (commentIndex >= 0) {
                        return line.substring(0, commentIndex).stripTrailing();
                    }
                    return line;
                })
                .filter(line -> !line.isBlank())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    public static SourceInfo dummySourceInfo() {
        Token dummyToken = new Token(TokenKind.EOF, null, 1, 1);
        return new SourceInfo(dummyToken, dummyToken);
    }
}
