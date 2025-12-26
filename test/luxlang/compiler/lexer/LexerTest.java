package luxlang.compiler.lexer;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static luxlang.compiler.utils.TokenListBuilder.tokenListBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {
    private final String LEXER_SUBDIRECTORY = "lexer";

    @Test
    public void keywords_all_types() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "keywords_all_types.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .typeVoid().space()
            .typeBool().space()
            .typeByte().space()
            .typeUbyte().space()
            .typeShort().space()
            .typeUshort().space()
            .typeInt().space()
            .typeUint().space()
            .typeLong().space()
            .typeUlong().space()
            .typeFloat().space()
            .typeDouble().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void keywords_control_flow() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "keywords_control_flow.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .keywordIf().space()
            .keywordElse().space()
            .keywordDo().space()
            .keywordWhile().space()
            .keywordFor().space()
            .keywordReturn().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void keywords_boolean_literals() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "keywords_boolean_literals.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .trueLiteral().space()
            .falseLiteral().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void identifiers() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "identifiers.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .identifier("x").space()
            .identifier("myVar").space()
            .identifier("var123").space()
            .identifier("test_var").space()
            .identifier("identifier").newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void integer_literals() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "integer_literals.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected =  tokenListBuilder()
            .integerLiteral("42").space()
            .integerLiteral("0").space()
            .integerLiteral("123").space()
            .integerLiteral("456u").space()
            .integerLiteral("789l").space()
            .integerLiteral("100ul").space()
            .integerLiteral("50s").space()
            .integerLiteral("25us").space()
            .integerLiteral("10b").space()
            .integerLiteral("5ub").newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void floating_point_literals() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "floating_point_literals.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .floatingPointLiteral("3.14").space()
            .floatingPointLiteral("0.5").space()
            .floatingPointLiteral("2.0f").space()
            .floatingPointLiteral("1.5d").space()
            .floatingPointLiteral("42.0F").space()
            .floatingPointLiteral("99.9D").space()
            .floatingPointLiteral(".5").space()
            .floatingPointLiteral(".25f").newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void arithmetic_operators() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "arithmetic_operators.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .add().space()
            .sub().space()
            .mult().space()
            .div().space()
            .mod().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void logical_operators() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "logical_operators.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .logicalNot().space()
            .logicalAnd().space()
            .logicalOr().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void bitwise_operators() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "bitwise_operators.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .bitwiseNot().space()
            .bitwiseAnd().space()
            .bitwiseOr().space()
            .bitwiseXor().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void comparison_operators() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "comparison_operators.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .equal().space()
            .notEqual().space()
            .less().space()
            .lessEqual().space()
            .greater().space()
            .greaterEqual().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void assignment_operator() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "assignment_operator.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
                .assign().newLine().eof()
                .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void punctuation() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "punctuation.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .leftParen().space()
            .rightParen().space()
            .leftBrace().space()
            .rightBrace().space()
            .semicolon().space()
            .comma().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void mixed_expression() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "mixed_expression.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .typeInt().space()
            .identifier("x").space()
            .assign().space()
            .integerLiteral("42").semicolon().newLine()
            .keywordIf().space()
            .leftParen().identifier("x").space()
            .greater().space()
            .integerLiteral("0").rightParen().space()
            .leftBrace().newLine()
            .tab().keywordReturn().space()
            .identifier("x").space()
            .mult().space()
            .integerLiteral("2").space()
            .add().space()
            .integerLiteral("1").semicolon().newLine()
            .rightBrace().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void function_declaration() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "function_declaration.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .typeInt().space()
            .identifier("add").leftParen().typeInt().space()
            .identifier("a").comma().space()
            .typeInt().space()
            .identifier("b").rightParen().space()
            .leftBrace().newLine()
            .tab().keywordReturn().space()
            .identifier("a").space()
            .add().space()
            .identifier("b").semicolon().newLine()
            .rightBrace().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void complex_expression() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "complex_expression.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .typeBool().space()
            .identifier("result").space()
            .assign().space()
            .leftParen().identifier("x").space()
            .greaterEqual().space()
            .integerLiteral("10").space()
            .logicalAnd().space()
            .identifier("y").space()
            .less().space()
            .integerLiteral("20").rightParen().space()
            .logicalOr().space()
            .leftParen().identifier("z").space()
            .equal().space()
            .integerLiteral("0").rightParen().semicolon().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void whitespace_handling() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "whitespace_handling.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .typeInt().whitespace(3)
            .identifier("x").whitespace(3)
            .assign().whitespace(3)
            .integerLiteral("42").whitespace(3)
            .semicolon().newLine().eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void multiline_code() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "multiline_code.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .typeInt().space()
            .identifier("x").space()
            .assign().space()
            .integerLiteral("1")
            .semicolon().newLine()
            .typeInt().space()
            .identifier("y").space()
            .assign().space()
            .integerLiteral("2")
            .semicolon().newLine()
            .typeInt().space()
            .identifier("z").space()
            .assign().space()
            .identifier("x").space()
            .add().space()
            .identifier("y").semicolon().newLine().eof().build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void empty_input() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "empty_input.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Success.class, result);
        List<Token> actual = ((LexingResult.Success) result).tokens();

        List<Token> expected = tokenListBuilder()
            .eof()
            .build();

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    public void unexpected_character() throws IOException {
        String input = TestUtils.readTestFile(LEXER_SUBDIRECTORY, "unexpected_character.lux");

        Lexer target = new Lexer(input);
        LexingResult result = target.lex();

        assertInstanceOf(LexingResult.Failure.class, result);
    }
}
