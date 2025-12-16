package luxlang.compiler.lexer;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;
import luxlang.compiler.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {

    @Test
    public void keywords_all_types() throws IOException {
        String input = TestUtils.readTestFile("keywords_all_types.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(13, tokens.size()); // 12 keywords + EOF
        assertEquals(TokenKind.VOID, tokens.get(0).kind());
        assertEquals(TokenKind.BOOL, tokens.get(1).kind());
        assertEquals(TokenKind.BYTE, tokens.get(2).kind());
        assertEquals(TokenKind.UBYTE, tokens.get(3).kind());
        assertEquals(TokenKind.SHORT, tokens.get(4).kind());
        assertEquals(TokenKind.USHORT, tokens.get(5).kind());
        assertEquals(TokenKind.INT, tokens.get(6).kind());
        assertEquals(TokenKind.UINT, tokens.get(7).kind());
        assertEquals(TokenKind.LONG, tokens.get(8).kind());
        assertEquals(TokenKind.ULONG, tokens.get(9).kind());
        assertEquals(TokenKind.FLOAT, tokens.get(10).kind());
        assertEquals(TokenKind.DOUBLE, tokens.get(11).kind());
        assertEquals(TokenKind.EOF, tokens.get(12).kind());
    }

    @Test
    public void keywords_control_flow() throws IOException {
        String input = TestUtils.readTestFile("keywords_control_flow.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(7, tokens.size()); // 6 keywords + EOF
        assertEquals(TokenKind.IF, tokens.get(0).kind());
        assertEquals(TokenKind.ELSE, tokens.get(1).kind());
        assertEquals(TokenKind.DO, tokens.get(2).kind());
        assertEquals(TokenKind.WHILE, tokens.get(3).kind());
        assertEquals(TokenKind.FOR, tokens.get(4).kind());
        assertEquals(TokenKind.RETURN, tokens.get(5).kind());
        assertEquals(TokenKind.EOF, tokens.get(6).kind());
    }

    @Test
    public void keywords_boolean_literals() throws IOException {
        String input = TestUtils.readTestFile("keywords_boolean_literals.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(3, tokens.size()); // 2 keywords + EOF
        assertEquals(TokenKind.TRUE, tokens.get(0).kind());
        assertEquals(TokenKind.FALSE, tokens.get(1).kind());
        assertEquals(TokenKind.EOF, tokens.get(2).kind());
    }

    @Test
    public void identifiers() throws IOException {
        String input = TestUtils.readTestFile("identifiers.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(6, tokens.size()); // 5 identifiers + EOF
        assertEquals(TokenKind.IDENTIFIER, tokens.get(0).kind());
        assertEquals("x", tokens.get(0).lexeme());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(1).kind());
        assertEquals("myVar", tokens.get(1).lexeme());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(2).kind());
        assertEquals("var123", tokens.get(2).lexeme());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(3).kind());
        assertEquals("test_var", tokens.get(3).lexeme());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(4).kind());
        assertEquals("identifier", tokens.get(4).lexeme());
        assertEquals(TokenKind.EOF, tokens.get(5).kind());
    }

    @Test
    public void integer_literals() throws IOException {
        String input = TestUtils.readTestFile("integer_literals.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(11, tokens.size()); // 10 integer literals + EOF
        for (int i = 0; i < 10; i++) {
            assertEquals(TokenKind.LITERAL_INTEGER, tokens.get(i).kind());
        }
        assertEquals("42", tokens.get(0).lexeme());
        assertEquals("0", tokens.get(1).lexeme());
        assertEquals("123", tokens.get(2).lexeme());
        assertEquals("456u", tokens.get(3).lexeme());
        assertEquals("789l", tokens.get(4).lexeme());
        assertEquals("100ul", tokens.get(5).lexeme());
        assertEquals("50s", tokens.get(6).lexeme());
        assertEquals("25us", tokens.get(7).lexeme());
        assertEquals("10b", tokens.get(8).lexeme());
        assertEquals("5ub", tokens.get(9).lexeme());
        assertEquals(TokenKind.EOF, tokens.get(10).kind());
    }

    @Test
    public void floating_point_literals() throws IOException {
        String input = TestUtils.readTestFile("floating_point_literals.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(9, tokens.size()); // 8 floating point literals + EOF
        for (int i = 0; i < 8; i++) {
            assertEquals(TokenKind.LITERAL_FLOATINGPT, tokens.get(i).kind());
        }
        assertEquals("3.14", tokens.get(0).lexeme());
        assertEquals("0.5", tokens.get(1).lexeme());
        assertEquals("2.0f", tokens.get(2).lexeme());
        assertEquals("1.5d", tokens.get(3).lexeme());
        assertEquals("42.0F", tokens.get(4).lexeme());
        assertEquals("99.9D", tokens.get(5).lexeme());
        assertEquals(".5", tokens.get(6).lexeme());
        assertEquals(".25f", tokens.get(7).lexeme());
        assertEquals(TokenKind.EOF, tokens.get(8).kind());
    }

    @Test
    public void arithmetic_operators() throws IOException {
        String input = TestUtils.readTestFile("arithmetic_operators.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(6, tokens.size()); // 5 operators + EOF
        assertEquals(TokenKind.ADD, tokens.get(0).kind());
        assertEquals(TokenKind.SUB, tokens.get(1).kind());
        assertEquals(TokenKind.MULT, tokens.get(2).kind());
        assertEquals(TokenKind.DIV, tokens.get(3).kind());
        assertEquals(TokenKind.MOD, tokens.get(4).kind());
        assertEquals(TokenKind.EOF, tokens.get(5).kind());
    }

    @Test
    public void logical_operators() throws IOException {
        String input = TestUtils.readTestFile("logical_operators.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(4, tokens.size()); // 3 operators + EOF
        assertEquals(TokenKind.LOGICAL_NOT, tokens.get(0).kind());
        assertEquals(TokenKind.LOGICAL_AND, tokens.get(1).kind());
        assertEquals(TokenKind.LOGICAL_OR, tokens.get(2).kind());
        assertEquals(TokenKind.EOF, tokens.get(3).kind());
    }

    @Test
    public void bitwise_operators() throws IOException {
        String input = TestUtils.readTestFile("bitwise_operators.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(5, tokens.size()); // 4 operators + EOF
        assertEquals(TokenKind.BITWISE_NOT, tokens.get(0).kind());
        assertEquals(TokenKind.BITWISE_AND, tokens.get(1).kind());
        assertEquals(TokenKind.BITWISE_OR, tokens.get(2).kind());
        assertEquals(TokenKind.BITWISE_XOR, tokens.get(3).kind());
        assertEquals(TokenKind.EOF, tokens.get(4).kind());
    }

    @Test
    public void comparison_operators() throws IOException {
        String input = TestUtils.readTestFile("comparison_operators.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(7, tokens.size()); // 6 operators + EOF
        assertEquals(TokenKind.EQUAL, tokens.get(0).kind());
        assertEquals(TokenKind.NOT_EQUAL, tokens.get(1).kind());
        assertEquals(TokenKind.LESS, tokens.get(2).kind());
        assertEquals(TokenKind.LESS_EQUAL, tokens.get(3).kind());
        assertEquals(TokenKind.GREATER, tokens.get(4).kind());
        assertEquals(TokenKind.GREATER_EQUAL, tokens.get(5).kind());
        assertEquals(TokenKind.EOF, tokens.get(6).kind());
    }

    @Test
    public void assignment_operator() throws IOException {
        String input = TestUtils.readTestFile("assignment_operator.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(2, tokens.size()); // 1 operator + EOF
        assertEquals(TokenKind.ASSIGN, tokens.get(0).kind());
        assertEquals(TokenKind.EOF, tokens.get(1).kind());
    }

    @Test
    public void punctuation() throws IOException {
        String input = TestUtils.readTestFile("punctuation.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(7, tokens.size()); // 6 punctuation + EOF
        assertEquals(TokenKind.LEFT_PAREN, tokens.get(0).kind());
        assertEquals(TokenKind.RIGHT_PAREN, tokens.get(1).kind());
        assertEquals(TokenKind.LEFT_BRACE, tokens.get(2).kind());
        assertEquals(TokenKind.RIGHT_BRACE, tokens.get(3).kind());
        assertEquals(TokenKind.SEMICOLON, tokens.get(4).kind());
        assertEquals(TokenKind.COMMA, tokens.get(5).kind());
        assertEquals(TokenKind.EOF, tokens.get(6).kind());
    }

    @Test
    public void mixed_expression() throws IOException {
        String input = TestUtils.readTestFile("mixed_expression.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(21, tokens.size());
        assertEquals(TokenKind.INT, tokens.get(0).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(1).kind());
        assertEquals(TokenKind.ASSIGN, tokens.get(2).kind());
        assertEquals(TokenKind.LITERAL_INTEGER, tokens.get(3).kind());
        assertEquals(TokenKind.SEMICOLON, tokens.get(4).kind());
        assertEquals(TokenKind.IF, tokens.get(5).kind());
        assertEquals(TokenKind.LEFT_PAREN, tokens.get(6).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(7).kind());
        assertEquals(TokenKind.GREATER, tokens.get(8).kind());
        assertEquals(TokenKind.LITERAL_INTEGER, tokens.get(9).kind());
        assertEquals(TokenKind.RIGHT_PAREN, tokens.get(10).kind());
        assertEquals(TokenKind.LEFT_BRACE, tokens.get(11).kind());
        assertEquals(TokenKind.RETURN, tokens.get(12).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(13).kind());
        assertEquals(TokenKind.MULT, tokens.get(14).kind());
        assertEquals(TokenKind.LITERAL_INTEGER, tokens.get(15).kind());
        assertEquals(TokenKind.ADD, tokens.get(16).kind());
        assertEquals(TokenKind.LITERAL_INTEGER, tokens.get(17).kind());
        assertEquals(TokenKind.SEMICOLON, tokens.get(18).kind());
        assertEquals(TokenKind.RIGHT_BRACE, tokens.get(19).kind());
        assertEquals(TokenKind.EOF, tokens.get(20).kind());
    }

    @Test
    public void function_declaration() throws IOException {
        String input = TestUtils.readTestFile("function_declaration.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(17, tokens.size());
        assertEquals(TokenKind.INT, tokens.get(0).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(1).kind());
        assertEquals("add", tokens.get(1).lexeme());
        assertEquals(TokenKind.LEFT_PAREN, tokens.get(2).kind());
        assertEquals(TokenKind.INT, tokens.get(3).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(4).kind());
        assertEquals("a", tokens.get(4).lexeme());
        assertEquals(TokenKind.COMMA, tokens.get(5).kind());
        assertEquals(TokenKind.INT, tokens.get(6).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(7).kind());
        assertEquals("b", tokens.get(7).lexeme());
        assertEquals(TokenKind.RIGHT_PAREN, tokens.get(8).kind());
        assertEquals(TokenKind.LEFT_BRACE, tokens.get(9).kind());
        assertEquals(TokenKind.RETURN, tokens.get(10).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(11).kind());
        assertEquals("a", tokens.get(11).lexeme());
        assertEquals(TokenKind.ADD, tokens.get(12).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(13).kind());
        assertEquals("b", tokens.get(13).lexeme());
        assertEquals(TokenKind.SEMICOLON, tokens.get(14).kind());
        assertEquals(TokenKind.RIGHT_BRACE, tokens.get(15).kind());
        assertEquals(TokenKind.EOF, tokens.get(16).kind());
    }

    @Test
    public void complex_expression() throws IOException {
        String input = TestUtils.readTestFile("complex_expression.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(20, tokens.size());
        assertEquals(TokenKind.BOOL, tokens.get(0).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(1).kind());
        assertEquals(TokenKind.ASSIGN, tokens.get(2).kind());
        assertEquals(TokenKind.LEFT_PAREN, tokens.get(3).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(4).kind());
        assertEquals(TokenKind.GREATER_EQUAL, tokens.get(5).kind());
        assertEquals(TokenKind.LITERAL_INTEGER, tokens.get(6).kind());
        assertEquals(TokenKind.LOGICAL_AND, tokens.get(7).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(8).kind());
        assertEquals(TokenKind.LESS, tokens.get(9).kind());
        assertEquals(TokenKind.LITERAL_INTEGER, tokens.get(10).kind());
        assertEquals(TokenKind.RIGHT_PAREN, tokens.get(11).kind());
        assertEquals(TokenKind.LOGICAL_OR, tokens.get(12).kind());
        assertEquals(TokenKind.LEFT_PAREN, tokens.get(13).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(14).kind());
        assertEquals(TokenKind.EQUAL, tokens.get(15).kind());
        assertEquals(TokenKind.LITERAL_INTEGER, tokens.get(16).kind());
        assertEquals(TokenKind.RIGHT_PAREN, tokens.get(17).kind());
        assertEquals(TokenKind.SEMICOLON, tokens.get(18).kind());
        assertEquals(TokenKind.EOF, tokens.get(19).kind());
    }

    @Test
    public void whitespace_handling() throws IOException {
        String input = TestUtils.readTestFile("whitespace_handling.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(6, tokens.size());
        assertEquals(TokenKind.INT, tokens.get(0).kind());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(1).kind());
        assertEquals(TokenKind.ASSIGN, tokens.get(2).kind());
        assertEquals(TokenKind.LITERAL_INTEGER, tokens.get(3).kind());
        assertEquals(TokenKind.SEMICOLON, tokens.get(4).kind());
        assertEquals(TokenKind.EOF, tokens.get(5).kind());
    }

    @Test
    public void multiline_code() throws IOException {
        String input = TestUtils.readTestFile("multiline_code.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(18, tokens.size());
        // First line
        assertEquals(TokenKind.INT, tokens.get(0).kind());
        assertEquals(1, tokens.get(0).line());
        assertEquals(TokenKind.IDENTIFIER, tokens.get(1).kind());
        assertEquals(1, tokens.get(1).line());
        // Second line
        assertEquals(TokenKind.INT, tokens.get(5).kind());
        assertEquals(2, tokens.get(5).line());
        // Third line
        assertEquals(TokenKind.INT, tokens.get(10).kind());
        assertEquals(3, tokens.get(10).line());
        // EOF
        assertEquals(TokenKind.EOF, tokens.get(17).kind());
    }

    @Test
    public void empty_input() throws IOException {
        String input = TestUtils.readTestFile("empty_input.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        assertEquals(1, tokens.size());
        assertEquals(TokenKind.EOF, tokens.get(0).kind());
    }

    @Test
    public void unexpected_character() throws IOException {
        String input = TestUtils.readTestFile("unexpected_character.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Failure.class, result);
    }
}
