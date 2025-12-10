package main.parser;

import main.lexer.Token;
import main.lexer.TokenKind;
import main.parser.errors.ParsingError;
import main.parser.errors.UnexpectedKindError;
import main.parser.nodes.*;
import main.parser.nodes.expressions.*;
import main.parser.nodes.expressions.BinaryOperation.BinaryOperationType;
import main.parser.nodes.expressions.UnaryOperation.UnaryOperationType;
import main.parser.nodes.statements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Parser {
    private final List<Token> tokens;
    private int i;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.i = 0;
    }

    public Program parse() {
        return parseProgram();
    }

    private Program parseProgram() {
        List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
        while (currentToken().kind() != TokenKind.EOF) {
            FunctionDeclaration function = parseFunctionDeclaration();
            functionDeclarations.add(function);
        }
        return new Program(functionDeclarations);
    }

    private FunctionDeclaration parseFunctionDeclaration() {
        Type returnType = parseType();

        expectCurrentTokenKind(TokenKind.IDENTIFIER);
        String name = currentToken().lexeme();
        increment();

        expectCurrentTokenKind(TokenKind.LEFT_PAREN);
        increment();

        List<Parameter> parameters = List.of();
        if (currentToken().kind() != TokenKind.RIGHT_PAREN) {
            parameters = parseParameterList();
        }

        expectCurrentTokenKind(TokenKind.RIGHT_PAREN);
        increment();

        CodeBlock body = parseCodeBlock();

        return new FunctionDeclaration(returnType, name, parameters, body);
    }

    private CodeBlock parseCodeBlock() {
        expectCurrentTokenKind(TokenKind.LEFT_BRACE);
        increment();

        List<Statement> statements = new ArrayList<>();
        while (currentToken().kind() != TokenKind.RIGHT_BRACE) {
            statements.add(parseStatement());
        }

        expectCurrentTokenKind(TokenKind.RIGHT_BRACE);
        increment();

        return new CodeBlock(statements);
    }

    private Statement parseStatement() {
        TokenKind kind = currentToken().kind();
        return switch (kind) {
            case LEFT_BRACE -> parseCodeBlock();
            case IF -> parseIfStatement();
            case WHILE -> parseWhileStatement();
            case DO -> parseDoWhileStatement();
            case FOR -> parseForStatement();
            case RETURN -> parseReturnStatement();
            case IDENTIFIER -> parseAssignmentStatement();
            default -> {
                if (kind.isTypeKind()) {
                    yield parseVariableDeclarationStatement();
                }
                throw new ParsingError("Not a statement", currentToken());
            }
        };
    }

    private VariableDeclaration parseVariableDeclarationStatement() {
        VariableDeclaration variableDeclaration = parseVariableDeclaration();

        expectCurrentTokenKind(TokenKind.SEMICOLON);
        increment();

        return variableDeclaration;
    }

    private Assignment parseAssignmentStatement() {
        Assignment assignment = parseAssignment();

        expectCurrentTokenKind(TokenKind.SEMICOLON);
        increment();

        return assignment;
    }

    private VariableDeclaration parseVariableDeclaration() {
        Type type = parseType();

        expectCurrentTokenKind(TokenKind.IDENTIFIER);
        String name = currentToken().lexeme();
        increment();

        if (currentToken().kind() != TokenKind.ASSIGN) {
            return new VariableDeclaration(type, name, Optional.empty());
        }

        expectCurrentTokenKind(TokenKind.ASSIGN);
        increment();

        Expression initialValue = parseExpression();

        return new VariableDeclaration(type, name, Optional.of(initialValue));
    }

    private Assignment parseAssignment() {
        expectCurrentTokenKind(TokenKind.IDENTIFIER);
        String variableName = currentToken().lexeme();
        increment();

        expectCurrentTokenKind(TokenKind.ASSIGN);
        increment();

        Expression value = parseExpression();

        return new Assignment(variableName, value);
    }

    private ReturnStatement parseReturnStatement() {
        expectCurrentTokenKind(TokenKind.RETURN);
        increment();

        Optional<Expression> value = Optional.empty();
        if (currentToken().kind() != TokenKind.SEMICOLON) {
            value = Optional.of(parseExpression());
        }

        expectCurrentTokenKind(TokenKind.SEMICOLON);
        increment();

        return new ReturnStatement(value);
    }

    private ForStatement parseForStatement() {
        expectCurrentTokenKind(TokenKind.FOR);
        increment();

        expectCurrentTokenKind(TokenKind.LEFT_PAREN);
        increment();

        Optional<ForStatement.Initializer> initializer = Optional.empty();
        if (currentToken().kind() != TokenKind.SEMICOLON) {
            initializer = Optional.of(parseForInitializer());
        }

        expectCurrentTokenKind(TokenKind.SEMICOLON);
        increment();

        Optional<Expression> condition = Optional.empty();
        if (currentToken().kind() != TokenKind.SEMICOLON) {
            condition = Optional.of(parseExpression());
        }

        expectCurrentTokenKind(TokenKind.SEMICOLON);
        increment();

        Optional<Assignment> update = Optional.empty();
        if (currentToken().kind() != TokenKind.RIGHT_PAREN) {
            update = Optional.of(parseAssignment());
        }

        expectCurrentTokenKind(TokenKind.RIGHT_PAREN);
        increment();

        Statement body = parseStatement();

        return new ForStatement(initializer, condition, update, body);
    }

    private ForStatement.Initializer parseForInitializer() {
        if (currentToken().kind().isTypeKind()) {
            return parseVariableDeclaration();
        }
        return parseAssignment();
    }

    private DoWhileStatement parseDoWhileStatement() {
        expectCurrentTokenKind(TokenKind.DO);
        increment();

        Statement body = parseStatement();

        expectCurrentTokenKind(TokenKind.WHILE);
        increment();

        expectCurrentTokenKind(TokenKind.LEFT_PAREN);
        increment();

        Expression condition = parseExpression();

        expectCurrentTokenKind(TokenKind.RIGHT_PAREN);
        increment();

        expectCurrentTokenKind(TokenKind.SEMICOLON);
        increment();

        return new DoWhileStatement(body, condition);
    }

    private WhileStatement parseWhileStatement() {
        expectCurrentTokenKind(TokenKind.WHILE);
        increment();

        expectCurrentTokenKind(TokenKind.LEFT_PAREN);
        increment();

        Expression condition = parseExpression();

        expectCurrentTokenKind(TokenKind.RIGHT_PAREN);
        increment();

        Statement body = parseStatement();

        return new WhileStatement(condition, body);
    }

    private IfStatement parseIfStatement() {
        expectCurrentTokenKind(TokenKind.IF);
        increment();

        expectCurrentTokenKind(TokenKind.LEFT_PAREN);
        increment();

        Expression condition = parseExpression();

        expectCurrentTokenKind(TokenKind.RIGHT_PAREN);
        increment();

        Statement body = parseStatement();

        Optional<Statement> elseBody = Optional.empty();
        if (currentToken().kind() == TokenKind.ELSE) {
            increment();
            elseBody = Optional.of(parseStatement());
        }

        return new IfStatement(condition, body, elseBody);
    }

    private Expression parseExpression() {
        return parseLogicalOr();
    }

    private Expression parseLogicalOr() {
        Expression result = parseLogicalAnd();

        while (currentToken().kind() == TokenKind.LOGICAL_OR) {
            increment();
            Expression left = result;
            Expression right = parseLogicalAnd();
            result = new BinaryOperation(BinaryOperationType.LOGICAL_OR, left, right);
        }

        return result;
    }

    private Expression parseLogicalAnd() {
        Expression result = parseBitwiseOr();

        while (currentToken().kind() == TokenKind.LOGICAL_AND) {
            increment();
            Expression left = result;
            Expression right = parseBitwiseOr();
            result = new BinaryOperation(BinaryOperationType.LOGICAL_AND, left, right);
        }

        return result;
    }

    private Expression parseBitwiseOr() {
        Expression result = parseBitwiseXor();

        while (currentToken().kind() == TokenKind.BITWISE_OR) {
            increment();
            Expression left = result;
            Expression right = parseBitwiseXor();
            result = new BinaryOperation(BinaryOperationType.BITWISE_OR, left, right);
        }

        return result;
    }

    private Expression parseBitwiseXor() {
        Expression result = parseBitwiseAnd();

        while (currentToken().kind() == TokenKind.BITWISE_XOR) {
            increment();
            Expression left = result;
            Expression right = parseBitwiseAnd();
            result = new BinaryOperation(BinaryOperationType.BITWISE_XOR, left, right);
        }

        return result;
    }

    private Expression parseBitwiseAnd() {
        Expression result = parseEquivalence();

        while (currentToken().kind() == TokenKind.BITWISE_AND) {
            increment();
            Expression left = result;
            Expression right = parseEquivalence();
            result = new BinaryOperation(BinaryOperationType.BITWISE_AND, left, right);
        }

        return result;
    }

    private Expression parseEquivalence() {
        Expression result = parseRelational();

        termLoop:
        while (true) {
            BinaryOperationType operation;
            switch(currentToken().kind()) {
                case EQUAL     -> operation = BinaryOperationType.EQUAL;
                case NOT_EQUAL -> operation = BinaryOperationType.NOT_EQUAL;
                default -> {
                    break termLoop;
                }
            }
            increment();

            Expression left = result;
            Expression right = parseRelational();
            result = new BinaryOperation(operation, left, right);
        }

        return result;
    }

    private Expression parseRelational() {
        Expression result = parseAdditive();

        termLoop:
        while (true) {
            BinaryOperationType operation;
            switch (currentToken().kind()) {
                case LESS          -> operation = BinaryOperationType.LESS;
                case LESS_EQUAL    -> operation = BinaryOperationType.LESS_EQUAL;
                case GREATER       -> operation = BinaryOperationType.GREATER;
                case GREATER_EQUAL -> operation = BinaryOperationType.GREATER_EQUAL;
                default -> {
                    break termLoop;
                }
            }
            increment();

            Expression left = result;
            Expression right = parseAdditive();
            result = new BinaryOperation(operation, left, right);
        }

        return result;
    }

    private Expression parseAdditive() {
        Expression result = parseMultiplicative();

        termLoop:
        while (true) {
            BinaryOperationType operation;
            switch (currentToken().kind()) {
                case ADD -> operation = BinaryOperationType.ADD;
                case SUB -> operation = BinaryOperationType.SUB;
                default -> {
                    break termLoop;
                }
            }
            increment();

            Expression left = result;
            Expression right = parseMultiplicative();
            result = new BinaryOperation(operation, left, right);
        }

        return result;
    }

    private Expression parseMultiplicative() {
        Expression result = parseUnary();

        termLoop:
        while (true) {
            BinaryOperationType operation;
            switch (currentToken().kind()) {
                case MULT -> operation = BinaryOperationType.MULT;
                case DIV  -> operation = BinaryOperationType.DIV;
                case MOD  -> operation = BinaryOperationType.MOD;
                default -> {
                    break termLoop;
                }
            }
            increment();

            Expression left = result;
            Expression right = parseUnary();
            result = new BinaryOperation(operation, left, right);
        }

        return result;
    }

    private Expression parseUnary() {
        UnaryOperationType operation;
        switch (currentToken().kind()) {
            case SUB         -> operation = UnaryOperationType.NEGATION;
            case LOGICAL_NOT -> operation = UnaryOperationType.LOGICAL_NOT;
            case BITWISE_NOT -> operation = UnaryOperationType.BITWISE_NOT;
            default -> {
                return parsePrimary();
            }
        }
        increment();

        Expression operand = parseUnary();
        return new UnaryOperation(operation, operand);
    }

    private Expression parsePrimary() {
        Token currentToken = currentToken();

        if (currentToken.kind() == TokenKind.IDENTIFIER) {
            String identifierName = currentToken.lexeme();
            increment();

            currentToken = currentToken();
            if (currentToken.kind() != TokenKind.LEFT_PAREN) {
                return new VariableExpression(identifierName);
            }

            increment();

            List<Expression> arguments = List.of();
            if (currentToken().kind() != TokenKind.RIGHT_PAREN) {
                arguments = parseArgumentList();
            }

            expectCurrentTokenKind(TokenKind.RIGHT_PAREN);
            increment();

            return new FunctionCall(identifierName, arguments);
        }

        increment();
        return switch (currentToken.kind()) {
            case LITERAL_INTEGER    -> new IntegerLiteral(currentToken.lexeme());
            case LITERAL_FLOATINGPT -> new FloatingPointLiteral(currentToken.lexeme());
            case TRUE               -> new BooleanLiteral(BooleanLiteral.Value.TRUE);
            case FALSE              -> new BooleanLiteral(BooleanLiteral.Value.FALSE);
            case LEFT_PAREN         -> {
                Expression result = parseExpression();

                expectCurrentTokenKind(TokenKind.RIGHT_PAREN);
                increment();

                yield result;
            }
            default                 -> throw new ParsingError("Could not parse expression", currentToken);
        };
    }

    private Type parseType() {
        Type type =  switch (currentToken().kind()) {
            case VOID -> Type.VOID;
            case BOOL -> Type.BOOL;
            case BYTE -> Type.BYTE;
            case UBYTE -> Type.UBYTE;
            case SHORT -> Type.SHORT;
            case USHORT -> Type.USHORT;
            case INT -> Type.INT;
            case UINT -> Type.UINT;
            case LONG -> Type.LONG;
            case ULONG -> Type.ULONG;
            case FLOAT -> Type.FLOAT;
            case DOUBLE -> Type.DOUBLE;
            default -> throw new ParsingError("Expected a type", currentToken());
        };
        increment();
        return type;
    }

    private List<Parameter> parseParameterList() {
        List<Parameter> parameterList = new ArrayList<>();

        do {
            Type type = parseType();

            expectCurrentTokenKind(TokenKind.IDENTIFIER);
            String name = currentToken().lexeme();
            increment();

            parameterList.add(new Parameter(type, name));
        } while (compareAndIncrement(TokenKind.COMMA));

        return parameterList;
    }

    private List<Expression> parseArgumentList() {
        List<Expression> argumentList = new ArrayList<>();

        do {
            argumentList.add(parseExpression());
        } while (compareAndIncrement(TokenKind.COMMA));

        return argumentList;
    }

    private void expectCurrentTokenKind(TokenKind kind) {
        Token currentToken = currentToken();
        if (currentToken.kind() != kind) {
            throw new UnexpectedKindError(currentToken, kind);
        }
    }

    private boolean compareAndIncrement(TokenKind kind) {
        Token currentToken = currentToken();
        if (currentToken.kind() == kind) {
            increment();
            return true;
        }
        return false;
    }

    private Token currentToken() {
        return tokens.get(i);
    }

    private void increment() {
        i++;
    }
}