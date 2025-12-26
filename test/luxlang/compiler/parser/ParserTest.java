package luxlang.compiler.parser;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.*;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation.BinaryOperationType;
import luxlang.compiler.parser.nodes.expressions.UnaryOperation.UnaryOperationType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static luxlang.compiler.utils.AstBuilder.*;
import static luxlang.compiler.utils.TokenListBuilder.tokenListBuilder;
import static org.assertj.core.api.Assertions.*;

public class ParserTest {

    @Test
    public void simple_function() {
        // int main() {
        //     return 0;
        // }
        List<Token> input = tokenListBuilder()
            .typeInt().identifier("main").leftParen().rightParen().space().leftBrace().newLine()
            .tab().keywordReturn().space().integerLiteral("0").semicolon().newLine()
            .rightBrace().newLine()
            .eof()
            .build();
        
        Parser parser = new Parser(input);
        ParsingResult result = parser.parse();
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program actual = ((ParsingResult.Success) result).program();

        Program expected = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(returnStmt(intLiteral("0")))
                .build()
        );

        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void function_with_parameters() {
        // int add(int a, int b) { return a + b; }
        List<Token> input = tokenListBuilder()
            .typeInt().space()
            .identifier("add").leftParen().typeInt().space()
            .identifier("a").comma().space()
            .typeInt().space()
            .identifier("b").rightParen().space()
            .leftBrace().space()
            .keywordReturn().space()
            .identifier("a").space()
            .add().space()
            .identifier("b").semicolon().space()
            .rightBrace()
            .eof()
            .build();
        
        Program expected = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("add")
                .param(Type.INT, "a")
                .param(Type.INT, "b")
                .statement(returnStmt(binaryOp(
                    BinaryOperationType.ADD,
                    varExpr("a"),
                    varExpr("b")
                )))
                .build()
        );
        
        Parser parser = new Parser(input);
        ParsingResult result = parser.parse();
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program actual = ((ParsingResult.Success) result).program();
        
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void variable_declaration_without_initializer() {
        // int main() { int x; return 0; }
        List<Token> input = tokenListBuilder()
            .typeInt().space()
            .identifier("main").leftParen().rightParen().space()
            .leftBrace().space()
            .typeInt().space()
            .identifier("x").semicolon().space()
            .keywordReturn().space()
            .integerLiteral("0").semicolon().space()
            .rightBrace().eof()
            .build();
        
        Program expected = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(varDecl(Type.INT, "x"))
                .statement(returnStmt(intLiteral("0")))
                .build()
        );
        
        Parser parser = new Parser(input);
        ParsingResult result = parser.parse();
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program actual = ((ParsingResult.Success) result).program();
        
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void variable_declaration_with_initializer() {
        // int main() { int y = 42; return y; }
        List<Token> input = tokenListBuilder()
            .typeInt().space()
            .identifier("main").leftParen().rightParen().space()
            .leftBrace().space()
            .typeInt().space()
            .identifier("y").space()
            .assign().space()
            .integerLiteral("42").semicolon().space()
            .keywordReturn().space()
            .identifier("y").semicolon().space()
            .rightBrace().eof()
            .build();
        
        Program expected = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(varDecl(Type.INT, "y", intLiteral("42")))
                .statement(returnStmt(varExpr("y")))
                .build()
        );
        
        Parser parser = new Parser(input);
        ParsingResult result = parser.parse();
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program actual = ((ParsingResult.Success) result).program();
        
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void if_statement() {
        // int main() { if (true) { return 1; } return 0; }
        List<Token> input = tokenListBuilder()
            .typeInt().space()
            .identifier("main").leftParen().rightParen().space()
            .leftBrace().space()
            .keywordIf().space()
            .leftParen().trueLiteral().rightParen().space()
            .leftBrace().space()
            .keywordReturn().space()
            .integerLiteral("1").semicolon().space()
            .rightBrace().space()
            .keywordReturn().space()
            .integerLiteral("0").semicolon().space()
            .rightBrace().eof()
            .build();

        Program expected = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(ifStmt(boolLiteral(true), codeBlock(returnStmt(intLiteral("1")))))
                .statement(returnStmt(intLiteral("0")))
                .build()
        );
        
        Parser parser = new Parser(input);
        ParsingResult result = parser.parse();
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program actual = ((ParsingResult.Success) result).program();
        
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void binary_expression() {
        // int main() { int a = 10 + 20; return a; }
        List<Token> input = tokenListBuilder()
            .typeInt().space()
            .identifier("main").leftParen().rightParen().space()
            .leftBrace().space()
            .typeInt().space()
            .identifier("a").space()
            .assign().space()
            .integerLiteral("10").space()
            .add().space()
            .integerLiteral("20").semicolon().space()
            .keywordReturn().space()
            .identifier("a").semicolon().space()
            .rightBrace().eof()
            .build();
        
        Program expected = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(varDecl(Type.INT, "a", binaryOp(
                    BinaryOperationType.ADD,
                    intLiteral("10"),
                    intLiteral("20")
                )))
                .statement(returnStmt(varExpr("a")))
                .build()
        );
        
        Parser parser = new Parser(input);
        ParsingResult result = parser.parse();
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program actual = ((ParsingResult.Success) result).program();
        
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void unary_expression() {
        // int main() { int x = -5; return x; }
        List<Token> input = tokenListBuilder()
            .typeInt().space()
            .identifier("main").leftParen().rightParen().space()
            .leftBrace().space()
            .typeInt().space()
            .identifier("x").space()
            .assign().space()
            .sub().integerLiteral("5").semicolon().space()
            .keywordReturn().space()
            .identifier("x").semicolon().space()
            .rightBrace().eof()
            .build();
        
        Program expected = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(varDecl(Type.INT, "x", unaryOp(
                    UnaryOperationType.NEGATION,
                    intLiteral("5")
                )))
                .statement(returnStmt(varExpr("x")))
                .build()
        );
        
        Parser parser = new Parser(input);
        ParsingResult result = parser.parse();
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program actual = ((ParsingResult.Success) result).program();
        
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }

    @Test
    public void while_loop() {
        // int main() { while (true) { return 1; } return 0; }
        List<Token> input = tokenListBuilder()
            .typeInt().space()
            .identifier("main").leftParen().rightParen().space()
            .leftBrace().space()
            .keywordWhile().space()
            .leftParen().trueLiteral().rightParen().space()
            .leftBrace().space()
            .keywordReturn().space()
            .integerLiteral("1").semicolon().space()
            .rightBrace().space()
            .keywordReturn().space()
            .integerLiteral("0").semicolon().space()
            .rightBrace().eof()
            .build();
        
        Program expected = program(
            functionBuilder()
                .returnType(Type.INT)
                .name("main")
                .statement(whileStmt(boolLiteral(true), codeBlock(returnStmt(intLiteral("1")))))
                .statement(returnStmt(intLiteral("0")))
                .build()
        );
        
        Parser parser = new Parser(input);
        ParsingResult result = parser.parse();
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program actual = ((ParsingResult.Success) result).program();
        
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFieldsMatchingRegexes(".*sourceInfo")
            .isEqualTo(expected);
    }
}
