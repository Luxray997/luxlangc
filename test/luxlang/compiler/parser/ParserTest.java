package luxlang.compiler.parser;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;
import luxlang.compiler.parser.nodes.*;
import luxlang.compiler.parser.nodes.expressions.*;
import luxlang.compiler.parser.nodes.statements.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static luxlang.compiler.parser.ParserTestUtils.tokens;
import static org.assertj.core.api.Assertions.*;

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
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program program = ((ParsingResult.Success) result).program();
        
        assertThat(program.functionDeclarations())
            .hasSize(1)
            .first()
            .satisfies(function -> {
                assertThat(function.name()).isEqualTo("main");
                assertThat(function.returnType()).isEqualTo(Type.INT);
                assertThat(function.parameters()).isEmpty();
                assertThat(function.body())
                    .isInstanceOf(CodeBlock.class)
                    .extracting(CodeBlock::statements)
                    .asList()
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ReturnStatement.class);
            });
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
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program program = ((ParsingResult.Success) result).program();
        
        assertThat(program.functionDeclarations())
            .hasSize(1)
            .first()
            .satisfies(function -> {
                assertThat(function.name()).isEqualTo("add");
                assertThat(function.returnType()).isEqualTo(Type.INT);
                assertThat(function.parameters())
                    .hasSize(2)
                    .satisfies(params -> {
                        assertThat(params.get(0))
                            .extracting(Parameter::name, Parameter::type)
                            .containsExactly("a", Type.INT);
                        assertThat(params.get(1))
                            .extracting(Parameter::name, Parameter::type)
                            .containsExactly("b", Type.INT);
                    });
            });
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
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program program = ((ParsingResult.Success) result).program();
        
        assertThat(program.functionDeclarations().get(0).body().statements())
            .hasSize(2)
            .first()
            .isInstanceOf(VariableDeclaration.class);
        
        VariableDeclaration varDecl = (VariableDeclaration) program.functionDeclarations().get(0).body().statements().get(0);
        assertThat(varDecl.name()).isEqualTo("x");
        assertThat(varDecl.type()).isEqualTo(Type.INT);
        assertThat(varDecl.initialValue()).isEmpty();
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
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program program = ((ParsingResult.Success) result).program();
        
        assertThat(program.functionDeclarations().get(0).body().statements())
            .hasSize(2)
            .first()
            .isInstanceOf(VariableDeclaration.class);
        
        VariableDeclaration varDecl = (VariableDeclaration) program.functionDeclarations().get(0).body().statements().get(0);
        assertThat(varDecl.name()).isEqualTo("y");
        assertThat(varDecl.type()).isEqualTo(Type.INT);
        assertThat(varDecl.initialValue())
            .isPresent()
            .hasValueSatisfying(value -> assertThat(value).isInstanceOf(IntegerLiteral.class));
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
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program program = ((ParsingResult.Success) result).program();
        
        assertThat(program.functionDeclarations().get(0).body().statements())
            .hasSize(2)
            .first()
            .isInstanceOf(IfStatement.class);
        
        IfStatement ifStmt = (IfStatement) program.functionDeclarations().get(0).body().statements().get(0);
        assertThat(ifStmt.condition()).isInstanceOf(BooleanLiteral.class);
        assertThat(ifStmt.body()).isInstanceOf(CodeBlock.class);
        assertThat(ifStmt.elseBody()).isEmpty();
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
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program program = ((ParsingResult.Success) result).program();
        
        assertThat(program.functionDeclarations().get(0).body().statements())
            .first()
            .isInstanceOf(VariableDeclaration.class);
        
        VariableDeclaration varDecl = (VariableDeclaration) program.functionDeclarations().get(0).body().statements().get(0);
        assertThat(varDecl.initialValue())
            .isPresent()
            .hasValueSatisfying(value -> {
                assertThat(value).isInstanceOf(BinaryOperation.class);
                BinaryOperation binOp = (BinaryOperation) value;
                assertThat(binOp.operation()).isEqualTo(BinaryOperation.BinaryOperationType.ADD);
                assertThat(binOp.left()).isInstanceOf(IntegerLiteral.class);
                assertThat(binOp.right()).isInstanceOf(IntegerLiteral.class);
            });
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
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program program = ((ParsingResult.Success) result).program();
        
        assertThat(program.functionDeclarations().get(0).body().statements())
            .first()
            .isInstanceOf(VariableDeclaration.class);
        
        VariableDeclaration varDecl = (VariableDeclaration) program.functionDeclarations().get(0).body().statements().get(0);
        assertThat(varDecl.initialValue())
            .isPresent()
            .hasValueSatisfying(value -> {
                assertThat(value).isInstanceOf(UnaryOperation.class);
                UnaryOperation unaryOp = (UnaryOperation) value;
                assertThat(unaryOp.operation()).isEqualTo(UnaryOperation.UnaryOperationType.NEGATION);
                assertThat(unaryOp.operand()).isInstanceOf(IntegerLiteral.class);
            });
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
        
        assertThat(result).isInstanceOf(ParsingResult.Success.class);
        Program program = ((ParsingResult.Success) result).program();
        
        assertThat(program.functionDeclarations().get(0).body().statements())
            .first()
            .isInstanceOf(WhileStatement.class);
        
        WhileStatement whileLoop = (WhileStatement) program.functionDeclarations().get(0).body().statements().get(0);
        assertThat(whileLoop.condition()).isNotNull();
        assertThat(whileLoop.body()).isInstanceOf(CodeBlock.class);
    }
}
