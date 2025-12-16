package luxlang.compiler.integration;

import luxlang.compiler.analysis.AnalysisResult;
import luxlang.compiler.analysis.Analyzer;
import luxlang.compiler.analysis.nodes.AnalyzedFunctionDeclaration;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.analysis.nodes.LocalVariable;
import luxlang.compiler.analysis.nodes.statements.AnalyzedCodeBlock;
import luxlang.compiler.lexer.Lexer;
import luxlang.compiler.lexer.LexingResult;
import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.Parser;
import luxlang.compiler.parser.ParsingResult;
import luxlang.compiler.parser.nodes.Program;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnalyzerIntegrationTest {
    private final String ANALYSIS_SUBDIRECTORY = "analysis";

    private AnalyzedProgram analyzeFile(String fileName) throws IOException {
        String input = TestUtils.readTestFile(ANALYSIS_SUBDIRECTORY, fileName);
        
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

    @Test
    public void simple_function() throws IOException {
        AnalyzedProgram program = analyzeFile("simple_function.lux");
        
        assertEquals(1, program.functionDeclarations().size());
        AnalyzedFunctionDeclaration function = program.functionDeclarations().get(0);
        assertEquals("main", function.name());
        assertEquals(Type.INT, function.returnType());
        assertEquals(0, function.parameters().size());
        assertInstanceOf(AnalyzedCodeBlock.class, function.body());
    }

    @Test
    public void function_call() throws IOException {
        AnalyzedProgram program = analyzeFile("function_call.lux");
        
        assertEquals(2, program.functionDeclarations().size());
        
        AnalyzedFunctionDeclaration addFunc = program.functionDeclarations().get(0);
        assertEquals("add", addFunc.name());
        assertEquals(Type.INT, addFunc.returnType());
        assertEquals(2, addFunc.parameters().size());
        
        AnalyzedFunctionDeclaration mainFunc = program.functionDeclarations().get(1);
        assertEquals("main", mainFunc.name());
        assertEquals(Type.INT, mainFunc.returnType());
        assertTrue(mainFunc.localVariables().size() > 0);
    }

    @Test
    public void variable_scope() throws IOException {
        AnalyzedProgram program = analyzeFile("variable_scope.lux");
        
        AnalyzedFunctionDeclaration function = program.functionDeclarations().get(0);
        List<LocalVariable> localVariables = function.localVariables();
        
        // Should have at least 2 local variables (x and y)
        assertTrue(localVariables.size() >= 2);
        
        // Check that variables have unique IDs
        long uniqueIds = localVariables.stream().map(LocalVariable::id).distinct().count();
        assertEquals(localVariables.size(), uniqueIds);
    }

    @Test
    public void type_checking() throws IOException {
        AnalyzedProgram program = analyzeFile("type_checking.lux");
        
        AnalyzedFunctionDeclaration function = program.functionDeclarations().get(0);
        List<LocalVariable> localVariables = function.localVariables();
        
        // Should have 4 local variables (x, y, z, b)
        assertEquals(4, localVariables.size());
        
        // Find variables by name and check their types
        LocalVariable x = localVariables.stream().filter(v -> v.name().equals("x")).findFirst().orElseThrow();
        assertEquals(Type.INT, x.type());
        
        LocalVariable b = localVariables.stream().filter(v -> v.name().equals("b")).findFirst().orElseThrow();
        assertEquals(Type.BOOL, b.type());
    }

    @Test
    public void control_flow() throws IOException {
        AnalyzedProgram program = analyzeFile("control_flow.lux");
        
        AnalyzedFunctionDeclaration function = program.functionDeclarations().get(0);
        assertNotNull(function.body());
        assertTrue(function.body().statements().size() > 0);
    }

    @Test
    public void for_loop() throws IOException {
        AnalyzedProgram program = analyzeFile("for_loop.lux");
        
        AnalyzedFunctionDeclaration function = program.functionDeclarations().get(0);
        List<LocalVariable> localVariables = function.localVariables();
        
        // Should have 2 local variables (sum and i)
        assertEquals(2, localVariables.size());
        
        // Both should be INT type
        assertTrue(localVariables.stream().allMatch(v -> v.type() == Type.INT));
    }

    @Test
    public void expressions() throws IOException {
        AnalyzedProgram program = analyzeFile("expressions.lux");
        
        AnalyzedFunctionDeclaration function = program.functionDeclarations().get(0);
        List<LocalVariable> localVariables = function.localVariables();
        
        // Should have 3 local variables (a, b, c)
        assertEquals(3, localVariables.size());
        
        // Check types
        LocalVariable a = localVariables.stream().filter(v -> v.name().equals("a")).findFirst().orElseThrow();
        assertEquals(Type.INT, a.type());
        
        LocalVariable c = localVariables.stream().filter(v -> v.name().equals("c")).findFirst().orElseThrow();
        assertEquals(Type.BOOL, c.type());
    }

    @Test
    public void literal_types() throws IOException {
        AnalyzedProgram program = analyzeFile("literal_types.lux");
        
        AnalyzedFunctionDeclaration function = program.functionDeclarations().get(0);
        List<LocalVariable> localVariables = function.localVariables();
        
        // Should have 5 local variables (i, l, f, d, b)
        assertEquals(5, localVariables.size());
        
        // Check individual types
        LocalVariable i = localVariables.stream().filter(v -> v.name().equals("i")).findFirst().orElseThrow();
        assertEquals(Type.INT, i.type());
        
        LocalVariable l = localVariables.stream().filter(v -> v.name().equals("l")).findFirst().orElseThrow();
        assertEquals(Type.LONG, l.type());
        
        LocalVariable f = localVariables.stream().filter(v -> v.name().equals("f")).findFirst().orElseThrow();
        assertEquals(Type.FLOAT, f.type());
        
        LocalVariable d = localVariables.stream().filter(v -> v.name().equals("d")).findFirst().orElseThrow();
        assertEquals(Type.DOUBLE, d.type());
        
        LocalVariable b = localVariables.stream().filter(v -> v.name().equals("b")).findFirst().orElseThrow();
        assertEquals(Type.BOOL, b.type());
    }

    @Test
    public void empty_return() throws IOException {
        AnalyzedProgram program = analyzeFile("empty_return.lux");
        
        AnalyzedFunctionDeclaration function = program.functionDeclarations().get(0);
        assertEquals("empty", function.name());
        assertEquals(Type.VOID, function.returnType());
        assertEquals(0, function.localVariables().size());
    }
}
