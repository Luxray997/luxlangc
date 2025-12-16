package luxlang.compiler.integration;

import luxlang.compiler.lexer.Lexer;
import luxlang.compiler.lexer.LexingResult;
import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.Parser;
import luxlang.compiler.parser.ParsingResult;
import luxlang.compiler.parser.nodes.*;
import luxlang.compiler.parser.nodes.expressions.*;
import luxlang.compiler.parser.nodes.statements.*;
import luxlang.compiler.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserIntegrationTest {
    private final String PARSER_SUBDIRECTORY = "parser";

    private Program parseFile(String fileName) throws IOException {
        String input = TestUtils.readTestFile(PARSER_SUBDIRECTORY, fileName);
        Lexer lexer = new Lexer(input);
        LexingResult lexingResult = lexer.lex();
        assertInstanceOf(LexingResult.Success.class, lexingResult);
        List<Token> tokens = ((LexingResult.Success) lexingResult).tokens();
        
        Parser parser = new Parser(tokens);
        ParsingResult result = parser.parse();
        assertInstanceOf(ParsingResult.Success.class, result);
        return ((ParsingResult.Success) result).program();
    }

    @Test
    public void simple_function() throws IOException {
        Program program = parseFile("simple_function.lux");
        
        assertEquals(1, program.functionDeclarations().size());
        FunctionDeclaration function = program.functionDeclarations().get(0);
        assertEquals("main", function.name());
        assertEquals(Type.INT, function.returnType());
        assertEquals(0, function.parameters().size());
        assertInstanceOf(CodeBlock.class, function.body());
    }

    @Test
    public void function_with_parameters() throws IOException {
        Program program = parseFile("function_with_parameters.lux");
        
        assertEquals(1, program.functionDeclarations().size());
        FunctionDeclaration function = program.functionDeclarations().get(0);
        assertEquals("add", function.name());
        assertEquals(Type.INT, function.returnType());
        assertEquals(2, function.parameters().size());
        
        Parameter param1 = function.parameters().get(0);
        assertEquals("a", param1.name());
        assertEquals(Type.INT, param1.type());
        
        Parameter param2 = function.parameters().get(1);
        assertEquals("b", param2.name());
        assertEquals(Type.INT, param2.type());
    }

    @Test
    public void variable_declaration() throws IOException {
        Program program = parseFile("variable_declaration.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        assertEquals(3, body.statements().size());
        
        Statement stmt1 = body.statements().get(0);
        assertInstanceOf(VariableDeclaration.class, stmt1);
        VariableDeclaration varDecl1 = (VariableDeclaration) stmt1;
        assertEquals("x", varDecl1.name());
        assertEquals(Type.INT, varDecl1.type());
        assertTrue(varDecl1.initialValue().isEmpty());
        
        Statement stmt2 = body.statements().get(1);
        assertInstanceOf(VariableDeclaration.class, stmt2);
        VariableDeclaration varDecl2 = (VariableDeclaration) stmt2;
        assertEquals("y", varDecl2.name());
        assertEquals(Type.INT, varDecl2.type());
        assertTrue(varDecl2.initialValue().isPresent());
    }

    @Test
    public void if_statement() throws IOException {
        Program program = parseFile("if_statement.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        assertEquals(2, body.statements().size());
        
        Statement stmt = body.statements().get(0);
        assertInstanceOf(IfStatement.class, stmt);
        IfStatement ifStmt = (IfStatement) stmt;
        assertInstanceOf(BooleanLiteral.class, ifStmt.condition());
        assertInstanceOf(CodeBlock.class, ifStmt.body());
        assertTrue(ifStmt.elseBody().isEmpty());
    }

    @Test
    public void if_else_statement() throws IOException {
        Program program = parseFile("if_else_statement.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        assertEquals(1, body.statements().size());
        
        Statement stmt = body.statements().get(0);
        assertInstanceOf(IfStatement.class, stmt);
        IfStatement ifStmt = (IfStatement) stmt;
        assertTrue(ifStmt.elseBody().isPresent());
    }

    @Test
    public void while_loop() throws IOException {
        Program program = parseFile("while_loop.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        Statement whileStmt = body.statements().get(1);
        assertInstanceOf(WhileStatement.class, whileStmt);
        WhileStatement whileLoop = (WhileStatement) whileStmt;
        assertNotNull(whileLoop.condition());
        assertInstanceOf(CodeBlock.class, whileLoop.body());
    }

    @Test
    public void do_while_loop() throws IOException {
        Program program = parseFile("do_while_loop.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        Statement doWhileStmt = body.statements().get(1);
        assertInstanceOf(DoWhileStatement.class, doWhileStmt);
        DoWhileStatement doWhileLoop = (DoWhileStatement) doWhileStmt;
        assertNotNull(doWhileLoop.condition());
        assertInstanceOf(CodeBlock.class, doWhileLoop.body());
    }

    @Test
    public void for_loop() throws IOException {
        Program program = parseFile("for_loop.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        Statement forStmt = body.statements().get(1);
        assertInstanceOf(ForStatement.class, forStmt);
        ForStatement forLoop = (ForStatement) forStmt;
        assertTrue(forLoop.initializer().isPresent());
        assertTrue(forLoop.condition().isPresent());
        assertTrue(forLoop.update().isPresent());
        assertInstanceOf(CodeBlock.class, forLoop.body());
    }

    @Test
    public void binary_expressions() throws IOException {
        Program program = parseFile("binary_expressions.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        for (int i = 0; i < 5; i++) {
            Statement stmt = body.statements().get(i);
            assertInstanceOf(VariableDeclaration.class, stmt);
            VariableDeclaration varDecl = (VariableDeclaration) stmt;
            assertTrue(varDecl.initialValue().isPresent());
            assertInstanceOf(BinaryOperation.class, varDecl.initialValue().get());
        }
    }

    @Test
    public void unary_expressions() throws IOException {
        Program program = parseFile("unary_expressions.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        Statement stmt1 = body.statements().get(0);
        assertInstanceOf(VariableDeclaration.class, stmt1);
        VariableDeclaration varDecl1 = (VariableDeclaration) stmt1;
        assertTrue(varDecl1.initialValue().isPresent());
        assertInstanceOf(UnaryOperation.class, varDecl1.initialValue().get());
        
        Statement stmt2 = body.statements().get(1);
        assertInstanceOf(VariableDeclaration.class, stmt2);
        VariableDeclaration varDecl2 = (VariableDeclaration) stmt2;
        assertTrue(varDecl2.initialValue().isPresent());
        assertInstanceOf(UnaryOperation.class, varDecl2.initialValue().get());
    }

    @Test
    public void comparison_expressions() throws IOException {
        Program program = parseFile("comparison_expressions.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        for (int i = 0; i < 6; i++) {
            Statement stmt = body.statements().get(i);
            assertInstanceOf(VariableDeclaration.class, stmt);
            VariableDeclaration varDecl = (VariableDeclaration) stmt;
            assertTrue(varDecl.initialValue().isPresent());
            assertInstanceOf(BinaryOperation.class, varDecl.initialValue().get());
        }
    }

    @Test
    public void logical_expressions() throws IOException {
        Program program = parseFile("logical_expressions.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        for (int i = 0; i < 2; i++) {
            Statement stmt = body.statements().get(i);
            assertInstanceOf(VariableDeclaration.class, stmt);
            VariableDeclaration varDecl = (VariableDeclaration) stmt;
            assertTrue(varDecl.initialValue().isPresent());
            assertInstanceOf(BinaryOperation.class, varDecl.initialValue().get());
        }
    }

    @Test
    public void multiple_functions() throws IOException {
        Program program = parseFile("multiple_functions.lux");
        
        assertEquals(3, program.functionDeclarations().size());
        
        FunctionDeclaration func1 = program.functionDeclarations().get(0);
        assertEquals("add", func1.name());
        
        FunctionDeclaration func2 = program.functionDeclarations().get(1);
        assertEquals("multiply", func2.name());
        
        FunctionDeclaration func3 = program.functionDeclarations().get(2);
        assertEquals("main", func3.name());
    }

    @Test
    public void nested_blocks() throws IOException {
        Program program = parseFile("nested_blocks.lux");
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        assertEquals(2, body.statements().size());
        
        Statement stmt = body.statements().get(0);
        assertInstanceOf(CodeBlock.class, stmt);
        CodeBlock nestedBlock = (CodeBlock) stmt;
        assertEquals(2, nestedBlock.statements().size());
        
        Statement innerStmt = nestedBlock.statements().get(1);
        assertInstanceOf(CodeBlock.class, innerStmt);
    }

    @Test
    public void empty_function() throws IOException {
        Program program = parseFile("empty_function.lux");
        
        assertEquals(1, program.functionDeclarations().size());
        FunctionDeclaration function = program.functionDeclarations().get(0);
        assertEquals("empty", function.name());
        assertEquals(Type.VOID, function.returnType());
        CodeBlock body = function.body();
        assertEquals(0, body.statements().size());
    }
}
