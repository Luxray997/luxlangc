package luxlang.compiler.parser;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;
import luxlang.compiler.parser.nodes.*;
import luxlang.compiler.parser.nodes.expressions.*;
import luxlang.compiler.parser.nodes.statements.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static luxlang.compiler.parser.ParserTestUtils.tokens;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    public void simple_function() {
        // int main() { return 0; }
        List<Token> tokens = tokens()
            .keyword(TokenKind.INT)
            .identifier("main")
            .punctuation(TokenKind.LEFT_PAREN)
            .punctuation(TokenKind.RIGHT_PAREN)
            .punctuation(TokenKind.LEFT_BRACE)
            .newLine()
            .keyword(TokenKind.RETURN)
            .intLiteral("0")
            .punctuation(TokenKind.SEMICOLON)
            .newLine()
            .punctuation(TokenKind.RIGHT_BRACE)
            .newLine()
            .eof()
            .build();
        
        Parser parser = new Parser(tokens);
        ParsingResult result = parser.parse();
        
        assertInstanceOf(ParsingResult.Success.class, result);
        Program program = ((ParsingResult.Success) result).program();
        
        assertEquals(1, program.functionDeclarations().size());
        FunctionDeclaration function = program.functionDeclarations().get(0);
        assertEquals("main", function.name());
        assertEquals(Type.INT, function.returnType());
        assertEquals(0, function.parameters().size());
        assertInstanceOf(CodeBlock.class, function.body());
        
        CodeBlock body = function.body();
        assertEquals(1, body.statements().size());
        assertInstanceOf(ReturnStatement.class, body.statements().get(0));
    }

    @Test
    public void function_with_parameters() {
        // int add(int a, int b) { return a + b; }
        List<Token> tokens = List.of(
            new Token(TokenKind.INT, "int", 1, 1),
            new Token(TokenKind.IDENTIFIER, "add", 1, 5),
            new Token(TokenKind.LEFT_PAREN, "(", 1, 8),
            new Token(TokenKind.INT, "int", 1, 9),
            new Token(TokenKind.IDENTIFIER, "a", 1, 13),
            new Token(TokenKind.COMMA, ",", 1, 14),
            new Token(TokenKind.INT, "int", 1, 16),
            new Token(TokenKind.IDENTIFIER, "b", 1, 20),
            new Token(TokenKind.RIGHT_PAREN, ")", 1, 21),
            new Token(TokenKind.LEFT_BRACE, "{", 1, 23),
            new Token(TokenKind.RETURN, "return", 2, 5),
            new Token(TokenKind.IDENTIFIER, "a", 2, 12),
            new Token(TokenKind.ADD, "+", 2, 14),
            new Token(TokenKind.IDENTIFIER, "b", 2, 16),
            new Token(TokenKind.SEMICOLON, ";", 2, 17),
            new Token(TokenKind.RIGHT_BRACE, "}", 3, 1),
            new Token(TokenKind.EOF, "", 4, 1)
        );
        
        Parser parser = new Parser(tokens);
        ParsingResult result = parser.parse();
        
        assertInstanceOf(ParsingResult.Success.class, result);
        Program program = ((ParsingResult.Success) result).program();
        
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
    public void variable_declaration_without_initializer() {
        // int main() { int x; return 0; }
        List<Token> tokens = List.of(
            new Token(TokenKind.INT, "int", 1, 1),
            new Token(TokenKind.IDENTIFIER, "main", 1, 5),
            new Token(TokenKind.LEFT_PAREN, "(", 1, 9),
            new Token(TokenKind.RIGHT_PAREN, ")", 1, 10),
            new Token(TokenKind.LEFT_BRACE, "{", 1, 12),
            new Token(TokenKind.INT, "int", 2, 5),
            new Token(TokenKind.IDENTIFIER, "x", 2, 9),
            new Token(TokenKind.SEMICOLON, ";", 2, 10),
            new Token(TokenKind.RETURN, "return", 3, 5),
            new Token(TokenKind.LITERAL_INTEGER, "0", 3, 12),
            new Token(TokenKind.SEMICOLON, ";", 3, 13),
            new Token(TokenKind.RIGHT_BRACE, "}", 4, 1),
            new Token(TokenKind.EOF, "", 5, 1)
        );
        
        Parser parser = new Parser(tokens);
        ParsingResult result = parser.parse();
        
        assertInstanceOf(ParsingResult.Success.class, result);
        Program program = ((ParsingResult.Success) result).program();
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        assertEquals(2, body.statements().size());
        
        Statement stmt1 = body.statements().get(0);
        assertInstanceOf(VariableDeclaration.class, stmt1);
        VariableDeclaration varDecl = (VariableDeclaration) stmt1;
        assertEquals("x", varDecl.name());
        assertEquals(Type.INT, varDecl.type());
        assertTrue(varDecl.initialValue().isEmpty());
    }

    @Test
    public void variable_declaration_with_initializer() {
        // int main() { int y = 42; return y; }
        List<Token> tokens = List.of(
            new Token(TokenKind.INT, "int", 1, 1),
            new Token(TokenKind.IDENTIFIER, "main", 1, 5),
            new Token(TokenKind.LEFT_PAREN, "(", 1, 9),
            new Token(TokenKind.RIGHT_PAREN, ")", 1, 10),
            new Token(TokenKind.LEFT_BRACE, "{", 1, 12),
            new Token(TokenKind.INT, "int", 2, 5),
            new Token(TokenKind.IDENTIFIER, "y", 2, 9),
            new Token(TokenKind.ASSIGN, "=", 2, 11),
            new Token(TokenKind.LITERAL_INTEGER, "42", 2, 13),
            new Token(TokenKind.SEMICOLON, ";", 2, 15),
            new Token(TokenKind.RETURN, "return", 3, 5),
            new Token(TokenKind.IDENTIFIER, "y", 3, 12),
            new Token(TokenKind.SEMICOLON, ";", 3, 13),
            new Token(TokenKind.RIGHT_BRACE, "}", 4, 1),
            new Token(TokenKind.EOF, "", 5, 1)
        );
        
        Parser parser = new Parser(tokens);
        ParsingResult result = parser.parse();
        
        assertInstanceOf(ParsingResult.Success.class, result);
        Program program = ((ParsingResult.Success) result).program();
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        assertEquals(2, body.statements().size());
        
        Statement stmt1 = body.statements().get(0);
        assertInstanceOf(VariableDeclaration.class, stmt1);
        VariableDeclaration varDecl = (VariableDeclaration) stmt1;
        assertEquals("y", varDecl.name());
        assertEquals(Type.INT, varDecl.type());
        assertTrue(varDecl.initialValue().isPresent());
        assertInstanceOf(IntegerLiteral.class, varDecl.initialValue().get());
    }

    @Test
    public void if_statement() {
        // int main() { if (true) { return 1; } return 0; }
        List<Token> tokens = List.of(
            new Token(TokenKind.INT, "int", 1, 1),
            new Token(TokenKind.IDENTIFIER, "main", 1, 5),
            new Token(TokenKind.LEFT_PAREN, "(", 1, 9),
            new Token(TokenKind.RIGHT_PAREN, ")", 1, 10),
            new Token(TokenKind.LEFT_BRACE, "{", 1, 12),
            new Token(TokenKind.IF, "if", 2, 5),
            new Token(TokenKind.LEFT_PAREN, "(", 2, 8),
            new Token(TokenKind.TRUE, "true", 2, 9),
            new Token(TokenKind.RIGHT_PAREN, ")", 2, 13),
            new Token(TokenKind.LEFT_BRACE, "{", 2, 15),
            new Token(TokenKind.RETURN, "return", 3, 9),
            new Token(TokenKind.LITERAL_INTEGER, "1", 3, 16),
            new Token(TokenKind.SEMICOLON, ";", 3, 17),
            new Token(TokenKind.RIGHT_BRACE, "}", 4, 5),
            new Token(TokenKind.RETURN, "return", 5, 5),
            new Token(TokenKind.LITERAL_INTEGER, "0", 5, 12),
            new Token(TokenKind.SEMICOLON, ";", 5, 13),
            new Token(TokenKind.RIGHT_BRACE, "}", 6, 1),
            new Token(TokenKind.EOF, "", 7, 1)
        );
        
        Parser parser = new Parser(tokens);
        ParsingResult result = parser.parse();
        
        assertInstanceOf(ParsingResult.Success.class, result);
        Program program = ((ParsingResult.Success) result).program();
        
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
    public void binary_expression() {
        // int main() { int a = 10 + 20; return a; }
        List<Token> tokens = List.of(
            new Token(TokenKind.INT, "int", 1, 1),
            new Token(TokenKind.IDENTIFIER, "main", 1, 5),
            new Token(TokenKind.LEFT_PAREN, "(", 1, 9),
            new Token(TokenKind.RIGHT_PAREN, ")", 1, 10),
            new Token(TokenKind.LEFT_BRACE, "{", 1, 12),
            new Token(TokenKind.INT, "int", 2, 5),
            new Token(TokenKind.IDENTIFIER, "a", 2, 9),
            new Token(TokenKind.ASSIGN, "=", 2, 11),
            new Token(TokenKind.LITERAL_INTEGER, "10", 2, 13),
            new Token(TokenKind.ADD, "+", 2, 16),
            new Token(TokenKind.LITERAL_INTEGER, "20", 2, 18),
            new Token(TokenKind.SEMICOLON, ";", 2, 20),
            new Token(TokenKind.RETURN, "return", 3, 5),
            new Token(TokenKind.IDENTIFIER, "a", 3, 12),
            new Token(TokenKind.SEMICOLON, ";", 3, 13),
            new Token(TokenKind.RIGHT_BRACE, "}", 4, 1),
            new Token(TokenKind.EOF, "", 5, 1)
        );
        
        Parser parser = new Parser(tokens);
        ParsingResult result = parser.parse();
        
        assertInstanceOf(ParsingResult.Success.class, result);
        Program program = ((ParsingResult.Success) result).program();
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        Statement stmt1 = body.statements().get(0);
        assertInstanceOf(VariableDeclaration.class, stmt1);
        VariableDeclaration varDecl = (VariableDeclaration) stmt1;
        assertTrue(varDecl.initialValue().isPresent());
        assertInstanceOf(BinaryOperation.class, varDecl.initialValue().get());
        
        BinaryOperation binOp = (BinaryOperation) varDecl.initialValue().get();
        assertEquals(BinaryOperation.BinaryOperationType.ADD, binOp.operation());
        assertInstanceOf(IntegerLiteral.class, binOp.left());
        assertInstanceOf(IntegerLiteral.class, binOp.right());
    }

    @Test
    public void unary_expression() {
        // int main() { int x = -5; return x; }
        List<Token> tokens = List.of(
            new Token(TokenKind.INT, "int", 1, 1),
            new Token(TokenKind.IDENTIFIER, "main", 1, 5),
            new Token(TokenKind.LEFT_PAREN, "(", 1, 9),
            new Token(TokenKind.RIGHT_PAREN, ")", 1, 10),
            new Token(TokenKind.LEFT_BRACE, "{", 1, 12),
            new Token(TokenKind.INT, "int", 2, 5),
            new Token(TokenKind.IDENTIFIER, "x", 2, 9),
            new Token(TokenKind.ASSIGN, "=", 2, 11),
            new Token(TokenKind.SUB, "-", 2, 13),
            new Token(TokenKind.LITERAL_INTEGER, "5", 2, 14),
            new Token(TokenKind.SEMICOLON, ";", 2, 15),
            new Token(TokenKind.RETURN, "return", 3, 5),
            new Token(TokenKind.IDENTIFIER, "x", 3, 12),
            new Token(TokenKind.SEMICOLON, ";", 3, 13),
            new Token(TokenKind.RIGHT_BRACE, "}", 4, 1),
            new Token(TokenKind.EOF, "", 5, 1)
        );
        
        Parser parser = new Parser(tokens);
        ParsingResult result = parser.parse();
        
        assertInstanceOf(ParsingResult.Success.class, result);
        Program program = ((ParsingResult.Success) result).program();
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        Statement stmt1 = body.statements().get(0);
        assertInstanceOf(VariableDeclaration.class, stmt1);
        VariableDeclaration varDecl = (VariableDeclaration) stmt1;
        assertTrue(varDecl.initialValue().isPresent());
        assertInstanceOf(UnaryOperation.class, varDecl.initialValue().get());
        
        UnaryOperation unaryOp = (UnaryOperation) varDecl.initialValue().get();
        assertEquals(UnaryOperation.UnaryOperationType.NEGATION, unaryOp.operation());
        assertInstanceOf(IntegerLiteral.class, unaryOp.operand());
    }

    @Test
    public void while_loop() {
        // int main() { while (true) { return 1; } return 0; }
        List<Token> tokens = List.of(
            new Token(TokenKind.INT, "int", 1, 1),
            new Token(TokenKind.IDENTIFIER, "main", 1, 5),
            new Token(TokenKind.LEFT_PAREN, "(", 1, 9),
            new Token(TokenKind.RIGHT_PAREN, ")", 1, 10),
            new Token(TokenKind.LEFT_BRACE, "{", 1, 12),
            new Token(TokenKind.WHILE, "while", 2, 5),
            new Token(TokenKind.LEFT_PAREN, "(", 2, 11),
            new Token(TokenKind.TRUE, "true", 2, 12),
            new Token(TokenKind.RIGHT_PAREN, ")", 2, 16),
            new Token(TokenKind.LEFT_BRACE, "{", 2, 18),
            new Token(TokenKind.RETURN, "return", 3, 9),
            new Token(TokenKind.LITERAL_INTEGER, "1", 3, 16),
            new Token(TokenKind.SEMICOLON, ";", 3, 17),
            new Token(TokenKind.RIGHT_BRACE, "}", 4, 5),
            new Token(TokenKind.RETURN, "return", 5, 5),
            new Token(TokenKind.LITERAL_INTEGER, "0", 5, 12),
            new Token(TokenKind.SEMICOLON, ";", 5, 13),
            new Token(TokenKind.RIGHT_BRACE, "}", 6, 1),
            new Token(TokenKind.EOF, "", 7, 1)
        );
        
        Parser parser = new Parser(tokens);
        ParsingResult result = parser.parse();
        
        assertInstanceOf(ParsingResult.Success.class, result);
        Program program = ((ParsingResult.Success) result).program();
        
        FunctionDeclaration function = program.functionDeclarations().get(0);
        CodeBlock body = function.body();
        
        Statement whileStmt = body.statements().get(0);
        assertInstanceOf(WhileStatement.class, whileStmt);
        WhileStatement whileLoop = (WhileStatement) whileStmt;
        assertNotNull(whileLoop.condition());
        assertInstanceOf(CodeBlock.class, whileLoop.body());
    }
}
