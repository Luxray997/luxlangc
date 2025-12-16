package luxlang.compiler.integration;

import luxlang.compiler.analysis.AnalysisResult;
import luxlang.compiler.analysis.Analyzer;
import luxlang.compiler.analysis.nodes.AnalyzedProgram;
import luxlang.compiler.ir.BasicBlock;
import luxlang.compiler.ir.IRBuilder;
import luxlang.compiler.ir.IRFunction;
import luxlang.compiler.ir.IRLocal;
import luxlang.compiler.ir.IRModule;
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

public class IRBuilderIntegrationTest {
    private final String IR_SUBDIRECTORY = "ir";

    private IRModule buildIR(String fileName) throws IOException {
        String input = TestUtils.readTestFile(IR_SUBDIRECTORY, fileName);
        
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
        
        IRBuilder builder = new IRBuilder(analyzedProgram);
        return builder.build();
    }

    @Test
    public void simple_function() throws IOException {
        IRModule module = buildIR("simple_function.lux");
        
        assertEquals(1, module.functions().size());
        IRFunction function = module.functions().get(0);
        assertEquals("main", function.name());
        assertEquals(Type.INT, function.returnType());
        assertEquals(0, function.parameterTypes().size());
        assertTrue(function.basicBlocks().size() >= 1);
        
        // Should have at least an entry block
        BasicBlock entryBlock = function.basicBlocks().get(0);
        assertNotNull(entryBlock);
        assertNotNull(entryBlock.terminator());
    }

    @Test
    public void arithmetic() throws IOException {
        IRModule module = buildIR("arithmetic.lux");
        
        IRFunction function = module.functions().get(0);
        assertEquals("main", function.name());
        
        // Should have local variables for a, b, c, d, e
        assertEquals(5, function.locals().size());
        assertTrue(function.locals().containsKey("a"));
        assertTrue(function.locals().containsKey("b"));
        assertTrue(function.locals().containsKey("c"));
        assertTrue(function.locals().containsKey("d"));
        assertTrue(function.locals().containsKey("e"));
        
        // Should have at least one basic block
        assertTrue(function.basicBlocks().size() >= 1);
    }

    @Test
    public void if_statement() throws IOException {
        IRModule module = buildIR("if_statement.lux");
        
        IRFunction function = module.functions().get(0);
        
        // If statement creates multiple basic blocks (condition, then, merge)
        assertTrue(function.basicBlocks().size() >= 3);
        
        // Entry block should exist
        BasicBlock entryBlock = function.basicBlocks().get(0);
        assertNotNull(entryBlock.terminator());
    }

    @Test
    public void if_else_statement() throws IOException {
        IRModule module = buildIR("if_else_statement.lux");
        
        IRFunction function = module.functions().get(0);
        
        // If-else statement creates multiple basic blocks (condition, then, else, merge)
        assertTrue(function.basicBlocks().size() >= 4);
    }

    @Test
    public void while_loop() throws IOException {
        IRModule module = buildIR("while_loop.lux");
        
        IRFunction function = module.functions().get(0);
        
        // While loop creates multiple basic blocks (entry, condition, body, exit)
        assertTrue(function.basicBlocks().size() >= 3);
        
        // Should have local variable 'i'
        assertTrue(function.locals().containsKey("i"));
    }

    @Test
    public void for_loop() throws IOException {
        IRModule module = buildIR("for_loop.lux");
        
        IRFunction function = module.functions().get(0);
        
        // For loop creates multiple basic blocks
        assertTrue(function.basicBlocks().size() >= 3);
        
        // Should have local variables 'sum' and 'i'
        assertTrue(function.locals().containsKey("sum"));
        assertTrue(function.locals().containsKey("i"));
    }

    @Test
    public void local_variables() throws IOException {
        IRModule module = buildIR("local_variables.lux");
        
        IRFunction function = module.functions().get(0);
        
        // Should have 3 local variables
        assertEquals(3, function.locals().size());
        assertTrue(function.locals().containsKey("x"));
        assertTrue(function.locals().containsKey("y"));
        assertTrue(function.locals().containsKey("z"));
        
        // Check that locals have correct types
        IRLocal x = function.locals().get("x");
        assertEquals(Type.INT, x.type());
    }

    @Test
    public void function_parameters() throws IOException {
        IRModule module = buildIR("function_parameters.lux");
        
        assertEquals(2, module.functions().size());
        
        IRFunction addFunc = module.functions().get(0);
        assertEquals("add", addFunc.name());
        assertEquals(2, addFunc.parameterTypes().size());
        assertEquals(Type.INT, addFunc.parameterTypes().get(0));
        assertEquals(Type.INT, addFunc.parameterTypes().get(1));
        
        // Parameters should also be in locals
        assertTrue(addFunc.locals().containsKey("a"));
        assertTrue(addFunc.locals().containsKey("b"));
    }

    @Test
    public void empty_function() throws IOException {
        IRModule module = buildIR("empty_function.lux");
        
        IRFunction function = module.functions().get(0);
        assertEquals("empty", function.name());
        assertEquals(Type.VOID, function.returnType());
        assertEquals(0, function.locals().size());
        
        // Should still have at least one basic block with a terminator
        assertTrue(function.basicBlocks().size() >= 1);
        assertNotNull(function.basicBlocks().get(0).terminator());
    }
}
