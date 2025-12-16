package luxlang.compiler.lexer;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;
import luxlang.compiler.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {
    private final String LEXER_SUBDIRECTORY = "lexer";

    @Test
    public void keywords_all_types() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "keywords_all_types.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.VOID, TokenKind.BOOL, TokenKind.BYTE, TokenKind.UBYTE,
            TokenKind.SHORT, TokenKind.USHORT, TokenKind.INT, TokenKind.UINT,
            TokenKind.LONG, TokenKind.ULONG, TokenKind.FLOAT, TokenKind.DOUBLE,
            TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void keywords_control_flow() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "keywords_control_flow.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.IF, TokenKind.ELSE, TokenKind.DO, TokenKind.WHILE,
            TokenKind.FOR, TokenKind.RETURN, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void keywords_boolean_literals() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "keywords_boolean_literals.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(TokenKind.TRUE, TokenKind.FALSE, TokenKind.EOF);
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void identifiers() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "identifiers.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.IDENTIFIER, TokenKind.IDENTIFIER, TokenKind.IDENTIFIER,
            TokenKind.IDENTIFIER, TokenKind.IDENTIFIER, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);

        List<String> expectedLexemes = List.of("x", "myVar", "var123", "test_var", "identifier");
        List<String> actualLexemes = tokens.stream().limit(5).map(Token::lexeme).toList();
        assertEquals(expectedLexemes, actualLexemes);
    }

    @Test
    public void integer_literals() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "integer_literals.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.LITERAL_INTEGER, TokenKind.LITERAL_INTEGER, TokenKind.LITERAL_INTEGER,
            TokenKind.LITERAL_INTEGER, TokenKind.LITERAL_INTEGER, TokenKind.LITERAL_INTEGER,
            TokenKind.LITERAL_INTEGER, TokenKind.LITERAL_INTEGER, TokenKind.LITERAL_INTEGER,
            TokenKind.LITERAL_INTEGER, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);

        List<String> expectedLexemes = List.of(
            "42", "0", "123", "456u", "789l", "100ul", "50s", "25us", "10b", "5ub"
        );
        List<String> actualLexemes = tokens.stream().limit(10).map(Token::lexeme).toList();
        assertEquals(expectedLexemes, actualLexemes);
    }

    @Test
    public void floating_point_literals() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "floating_point_literals.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.LITERAL_FLOATINGPT, TokenKind.LITERAL_FLOATINGPT, TokenKind.LITERAL_FLOATINGPT,
            TokenKind.LITERAL_FLOATINGPT, TokenKind.LITERAL_FLOATINGPT, TokenKind.LITERAL_FLOATINGPT,
            TokenKind.LITERAL_FLOATINGPT, TokenKind.LITERAL_FLOATINGPT, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);

        List<String> expectedLexemes = List.of(
            "3.14", "0.5", "2.0f", "1.5d", "42.0F", "99.9D", ".5", ".25f"
        );
        List<String> actualLexemes = tokens.stream().limit(8).map(Token::lexeme).toList();
        assertEquals(expectedLexemes, actualLexemes);
    }

    @Test
    public void arithmetic_operators() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "arithmetic_operators.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.ADD, TokenKind.SUB, TokenKind.MULT, TokenKind.DIV, TokenKind.MOD, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void logical_operators() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "logical_operators.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.LOGICAL_NOT, TokenKind.LOGICAL_AND, TokenKind.LOGICAL_OR, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void bitwise_operators() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "bitwise_operators.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.BITWISE_NOT, TokenKind.BITWISE_AND, TokenKind.BITWISE_OR,
            TokenKind.BITWISE_XOR, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void comparison_operators() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "comparison_operators.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.EQUAL, TokenKind.NOT_EQUAL, TokenKind.LESS, TokenKind.LESS_EQUAL,
            TokenKind.GREATER, TokenKind.GREATER_EQUAL, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void assignment_operator() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "assignment_operator.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(TokenKind.ASSIGN, TokenKind.EOF);
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void punctuation() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "punctuation.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.LEFT_PAREN, TokenKind.RIGHT_PAREN, TokenKind.LEFT_BRACE,
            TokenKind.RIGHT_BRACE, TokenKind.SEMICOLON, TokenKind.COMMA, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void mixed_expression() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "mixed_expression.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.INT, TokenKind.IDENTIFIER, TokenKind.ASSIGN, TokenKind.LITERAL_INTEGER,
            TokenKind.SEMICOLON, TokenKind.IF, TokenKind.LEFT_PAREN, TokenKind.IDENTIFIER,
            TokenKind.GREATER, TokenKind.LITERAL_INTEGER, TokenKind.RIGHT_PAREN, TokenKind.LEFT_BRACE,
            TokenKind.RETURN, TokenKind.IDENTIFIER, TokenKind.MULT, TokenKind.LITERAL_INTEGER,
            TokenKind.ADD, TokenKind.LITERAL_INTEGER, TokenKind.SEMICOLON, TokenKind.RIGHT_BRACE,
            TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void function_declaration() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "function_declaration.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.INT, TokenKind.IDENTIFIER, TokenKind.LEFT_PAREN, TokenKind.INT,
            TokenKind.IDENTIFIER, TokenKind.COMMA, TokenKind.INT, TokenKind.IDENTIFIER,
            TokenKind.RIGHT_PAREN, TokenKind.LEFT_BRACE, TokenKind.RETURN, TokenKind.IDENTIFIER,
            TokenKind.ADD, TokenKind.IDENTIFIER, TokenKind.SEMICOLON, TokenKind.RIGHT_BRACE,
            TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);

        List<String> expectedIdentifierLexemes = List.of("add", "a", "b", "a", "b");
        List<String> actualIdentifierLexemes = tokens.stream()
            .filter(t -> t.kind() == TokenKind.IDENTIFIER)
            .map(Token::lexeme)
            .toList();
        assertEquals(expectedIdentifierLexemes, actualIdentifierLexemes);
    }

    @Test
    public void complex_expression() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "complex_expression.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.BOOL, TokenKind.IDENTIFIER, TokenKind.ASSIGN, TokenKind.LEFT_PAREN,
            TokenKind.IDENTIFIER, TokenKind.GREATER_EQUAL, TokenKind.LITERAL_INTEGER, TokenKind.LOGICAL_AND,
            TokenKind.IDENTIFIER, TokenKind.LESS, TokenKind.LITERAL_INTEGER, TokenKind.RIGHT_PAREN,
            TokenKind.LOGICAL_OR, TokenKind.LEFT_PAREN, TokenKind.IDENTIFIER, TokenKind.EQUAL,
            TokenKind.LITERAL_INTEGER, TokenKind.RIGHT_PAREN, TokenKind.SEMICOLON, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void whitespace_handling() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "whitespace_handling.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.INT, TokenKind.IDENTIFIER, TokenKind.ASSIGN,
            TokenKind.LITERAL_INTEGER, TokenKind.SEMICOLON, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void multiline_code() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "multiline_code.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(
            TokenKind.INT, TokenKind.IDENTIFIER, TokenKind.ASSIGN, TokenKind.LITERAL_INTEGER, TokenKind.SEMICOLON,
            TokenKind.INT, TokenKind.IDENTIFIER, TokenKind.ASSIGN, TokenKind.LITERAL_INTEGER, TokenKind.SEMICOLON,
            TokenKind.INT, TokenKind.IDENTIFIER, TokenKind.ASSIGN, TokenKind.IDENTIFIER, TokenKind.ADD,
            TokenKind.IDENTIFIER, TokenKind.SEMICOLON, TokenKind.EOF
        );
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);

        List<Integer> expectedIntLines = List.of(1, 2, 3);
        List<Integer> actualIntLines = tokens.stream()
            .filter(t -> t.kind() == TokenKind.INT)
            .map(Token::line)
            .toList();
        assertEquals(expectedIntLines, actualIntLines);
    }

    @Test
    public void empty_input() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "empty_input.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> tokens = ((LexingResult.Success) result).tokens();

        List<TokenKind> expectedKinds = List.of(TokenKind.EOF);
        List<TokenKind> actualKinds = tokens.stream().map(Token::kind).toList();
        assertEquals(expectedKinds, actualKinds);
    }

    @Test
    public void unexpected_character() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "unexpected_character.lux");
        Lexer lexer = new Lexer(input);
        LexingResult result = lexer.lex();

        assertInstanceOf(LexingResult.Failure.class, result);
    }
}
