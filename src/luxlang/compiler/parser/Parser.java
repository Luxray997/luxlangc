package luxlang.compiler.parser;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;
import luxlang.compiler.parser.errors.*;
import luxlang.compiler.parser.nodes.expressions.*;
import luxlang.compiler.parser.nodes.statements.*;
import luxlang.compiler.parser.nodes.FunctionDeclaration;
import luxlang.compiler.parser.nodes.Parameter;
import luxlang.compiler.parser.nodes.Program;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.expressions.BinaryOperation.BinaryOperationType;
import luxlang.compiler.parser.nodes.expressions.UnaryOperation.UnaryOperationType;
import luxlang.compiler.parser.objects.SourceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Parser {
    private final List<Token> tokens;
    private final List<ParsingError> errors;
    private int i;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.errors = new ArrayList<>();
        this.i = 0;
    }

    public ParsingResult parse() {
        try {
            return parseProgram();
        } catch (FatalParsingException exception) {
            return ParsingResult.failure(errors);
        }
    }

    private ParsingResult parseProgram() {
        List<FunctionDeclaration> functionDeclarations = new ArrayList<>();
        while (currentToken().kind() != TokenKind.EOF) {
            FunctionDeclaration function = parseFunctionDeclaration();
            functionDeclarations.add(function);
        }

        if (errors.isEmpty()) {
            var program = new Program(functionDeclarations);
            return ParsingResult.success(program);
        }
        return ParsingResult.failure(errors);
    }

    private FunctionDeclaration parseFunctionDeclaration() {
        Token firstToken = currentToken();
        Type returnType = parseType();

        expectCurrentTokenKind(TokenKind.IDENTIFIER);
        String name = currentToken().lexeme();
        increment();

        expectAndIncrement(TokenKind.LEFT_PAREN);

        List<Parameter> parameters = List.of();
        if (currentToken().kind() != TokenKind.RIGHT_PAREN) {
            parameters = parseParameterList();
        }

        expectAndIncrement(TokenKind.RIGHT_PAREN);

        CodeBlock body = parseCodeBlock();
        Token lastToken = body.sourceInfo().lastToken();
        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new FunctionDeclaration(returnType, name, parameters, body, sourceInfo);
    }

    private CodeBlock parseCodeBlock() {
        expectCurrentTokenKind(TokenKind.LEFT_BRACE);
        Token firstToken = consume();

        List<Statement> statements = new ArrayList<>();
        while (currentToken().kind() != TokenKind.RIGHT_BRACE) {
            statements.add(parseStatement());
        }

        expectCurrentTokenKind(TokenKind.RIGHT_BRACE);

        Token lastToken = consume();
        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new CodeBlock(statements, sourceInfo);
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
                errors.add(new NotAStatementError(currentToken()));
                throw new FatalParsingException();
            }
        };
    }

    private VariableDeclaration parseVariableDeclarationStatement() {
        VariableDeclaration variableDeclaration = parseVariableDeclaration();

        expectAndIncrement(TokenKind.SEMICOLON);

        return variableDeclaration;
    }

    private Assignment parseAssignmentStatement() {
        Assignment assignment = parseAssignment();

        expectAndIncrement(TokenKind.SEMICOLON);

        return assignment;
    }

    private VariableDeclaration parseVariableDeclaration() {
        Token firstToken = currentToken();
        Type type = parseType();

        expectCurrentTokenKind(TokenKind.IDENTIFIER);
        Token lastToken = consume();
        String name = lastToken.lexeme();

        if (currentToken().kind() != TokenKind.ASSIGN) {
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            return new VariableDeclaration(type, name, Optional.empty(), sourceInfo);
        }

        expectAndIncrement(TokenKind.ASSIGN);

        Expression initialValue = parseExpression();

        lastToken = initialValue.sourceInfo().lastToken();

        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new VariableDeclaration(type, name, Optional.of(initialValue), sourceInfo);
    }

    private Assignment parseAssignment() {
        expectCurrentTokenKind(TokenKind.IDENTIFIER);
        Token firstToken = consume();
        String variableName = firstToken.lexeme();

        expectAndIncrement(TokenKind.ASSIGN);

        Expression value = parseExpression();

        Token lastToken = value.sourceInfo().lastToken();

        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new Assignment(variableName, value, sourceInfo);
    }

    private ReturnStatement parseReturnStatement() {
        expectCurrentTokenKind(TokenKind.RETURN);
        Token firstToken = consume();

        Optional<Expression> value = Optional.empty();
        if (currentToken().kind() != TokenKind.SEMICOLON) {
            value = Optional.of(parseExpression());
        }

        expectCurrentTokenKind(TokenKind.SEMICOLON);

        Token lastToken = consume();

        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new ReturnStatement(value, sourceInfo);
    }

    private ForStatement parseForStatement() {
        expectCurrentTokenKind(TokenKind.FOR);
        Token firstToken = consume();

        expectAndIncrement(TokenKind.LEFT_PAREN);

        Optional<ForStatement.Initializer> initializer = Optional.empty();
        if (currentToken().kind() != TokenKind.SEMICOLON) {
            initializer = Optional.of(parseForInitializer());
        }

        expectAndIncrement(TokenKind.SEMICOLON);

        Optional<Expression> condition = Optional.empty();
        if (currentToken().kind() != TokenKind.SEMICOLON) {
            condition = Optional.of(parseExpression());
        }

        expectAndIncrement(TokenKind.SEMICOLON);

        Optional<Assignment> update = Optional.empty();
        if (currentToken().kind() != TokenKind.RIGHT_PAREN) {
            update = Optional.of(parseAssignment());
        }

        expectAndIncrement(TokenKind.RIGHT_PAREN);

        Statement body = parseStatement();

        Token lastToken = body.sourceInfo().lastToken();

        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new ForStatement(initializer, condition, update, body, sourceInfo);
    }

    private ForStatement.Initializer parseForInitializer() {
        if (currentToken().kind().isTypeKind()) {
            return parseVariableDeclaration();
        }
        return parseAssignment();
    }

    private DoWhileStatement parseDoWhileStatement() {
        expectCurrentTokenKind(TokenKind.DO);
        Token firstToken = consume();

        Statement body = parseStatement();

        expectAndIncrement(TokenKind.WHILE);

        expectAndIncrement(TokenKind.LEFT_PAREN);

        Expression condition = parseExpression();

        expectAndIncrement(TokenKind.RIGHT_PAREN);

        expectCurrentTokenKind(TokenKind.SEMICOLON);

        Token lastToken = consume();

        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new DoWhileStatement(body, condition, sourceInfo);
    }

    private WhileStatement parseWhileStatement() {
        expectCurrentTokenKind(TokenKind.WHILE);
        Token firstToken = consume();

        expectAndIncrement(TokenKind.LEFT_PAREN);

        Expression condition = parseExpression();

        expectAndIncrement(TokenKind.RIGHT_PAREN);

        Statement body = parseStatement();

        Token lastToken = body.sourceInfo().lastToken();

        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new WhileStatement(condition, body, sourceInfo);
    }

    private IfStatement parseIfStatement() {
        expectCurrentTokenKind(TokenKind.IF);
        Token firstToken = consume();

        expectAndIncrement(TokenKind.LEFT_PAREN);

        Expression condition = parseExpression();

        expectAndIncrement(TokenKind.RIGHT_PAREN);

        Statement body = parseStatement();
        Token lastToken = body.sourceInfo().lastToken();
        Optional<Statement> elseBody = Optional.empty();
        if (currentToken().kind() == TokenKind.ELSE) {
            increment();
            var elseBodyStatement = parseStatement();
            lastToken = elseBodyStatement.sourceInfo().lastToken();
            elseBody = Optional.of(elseBodyStatement);
        }

        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new IfStatement(condition, body, elseBody, sourceInfo);
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

            Token firstToken = left.sourceInfo().firstToken();
            Token lastToken = right.sourceInfo().lastToken();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            result = new BinaryOperation(BinaryOperationType.LOGICAL_OR, left, right, sourceInfo);
        }

        return result;
    }

    private Expression parseLogicalAnd() {
        Expression result = parseBitwiseOr();

        while (currentToken().kind() == TokenKind.LOGICAL_AND) {
            increment();
            Expression left = result;
            Expression right = parseBitwiseOr();

            Token firstToken = left.sourceInfo().firstToken();
            Token lastToken = right.sourceInfo().lastToken();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            result = new BinaryOperation(BinaryOperationType.LOGICAL_AND, left, right, sourceInfo);
        }

        return result;
    }

    private Expression parseBitwiseOr() {
        Expression result = parseBitwiseXor();

        while (currentToken().kind() == TokenKind.BITWISE_OR) {
            increment();
            Expression left = result;
            Expression right = parseBitwiseXor();

            Token firstToken = left.sourceInfo().firstToken();
            Token lastToken = right.sourceInfo().lastToken();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            result = new BinaryOperation(BinaryOperationType.BITWISE_OR, left, right, sourceInfo);
        }

        return result;
    }

    private Expression parseBitwiseXor() {
        Expression result = parseBitwiseAnd();

        while (currentToken().kind() == TokenKind.BITWISE_XOR) {
            increment();
            Expression left = result;
            Expression right = parseBitwiseAnd();

            Token firstToken = left.sourceInfo().firstToken();
            Token lastToken = right.sourceInfo().lastToken();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            result = new BinaryOperation(BinaryOperationType.BITWISE_XOR, left, right, sourceInfo);
        }

        return result;
    }

    private Expression parseBitwiseAnd() {
        Expression result = parseEquivalence();

        while (currentToken().kind() == TokenKind.BITWISE_AND) {
            increment();
            Expression left = result;
            Expression right = parseEquivalence();

            Token firstToken = left.sourceInfo().firstToken();
            Token lastToken = right.sourceInfo().lastToken();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            result = new BinaryOperation(BinaryOperationType.BITWISE_AND, left, right, sourceInfo);
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

            Token firstToken = left.sourceInfo().firstToken();
            Token lastToken = right.sourceInfo().lastToken();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            result = new BinaryOperation(operation, left, right, sourceInfo);
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

            Token firstToken = left.sourceInfo().firstToken();
            Token lastToken = right.sourceInfo().lastToken();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            result = new BinaryOperation(operation, left, right, sourceInfo);
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

            Token firstToken = left.sourceInfo().firstToken();
            Token lastToken = right.sourceInfo().lastToken();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            result = new BinaryOperation(operation, left, right, sourceInfo);
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

            Token firstToken = left.sourceInfo().firstToken();
            Token lastToken = right.sourceInfo().lastToken();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            result = new BinaryOperation(operation, left, right, sourceInfo);
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
        Token firstToken = consume();

        Expression operand = parseUnary();

        Token lastToken = operand.sourceInfo().lastToken();
        var sourceInfo = new SourceInfo(firstToken, lastToken);
        return new UnaryOperation(operation, operand, sourceInfo);
    }

    private Expression parsePrimary() {
        Token currentToken = currentToken();

        if (currentToken.kind() == TokenKind.IDENTIFIER) {
            Token firstToken = consume();
            String identifierName = firstToken.lexeme();

            currentToken = currentToken();
            if (currentToken.kind() != TokenKind.LEFT_PAREN) {
                var sourceInfo = new SourceInfo(firstToken, firstToken);
                return new VariableExpression(identifierName, sourceInfo);
            }

            increment();

            List<Expression> arguments = List.of();
            if (currentToken().kind() != TokenKind.RIGHT_PAREN) {
                arguments = parseArgumentList();
            }

            expectCurrentTokenKind(TokenKind.RIGHT_PAREN);

            Token lastToken = consume();
            var sourceInfo = new SourceInfo(firstToken, lastToken);
            return new FunctionCall(identifierName, arguments, sourceInfo);
        }

        Token firstToken = consume();
        var sourceInfo = new SourceInfo(firstToken, firstToken);
        return switch (currentToken.kind()) {
            case LITERAL_INTEGER    -> new IntegerLiteral(currentToken.lexeme(), sourceInfo);
            case LITERAL_FLOATINGPT -> new FloatingPointLiteral(currentToken.lexeme(), sourceInfo);
            case TRUE               -> new BooleanLiteral(BooleanLiteral.Value.TRUE, sourceInfo);
            case FALSE              -> new BooleanLiteral(BooleanLiteral.Value.FALSE, sourceInfo);
            case LEFT_PAREN         -> {
                Expression result = parseExpression();

                expectAndIncrement(TokenKind.RIGHT_PAREN);

                yield result;
            }
            default -> {
                errors.add(new NotAnExpressionError(currentToken));
                throw new FatalParsingException();
            }
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
            default -> {
                errors.add(new NotATypeError(currentToken()));
                yield Type.VOID;
            }
        };
        increment();
        return type;
    }

    private List<Parameter> parseParameterList() {
        List<Parameter> parameterList = new ArrayList<>();

        do {
            Token firstToken = currentToken();
            Type type = parseType();

            expectCurrentTokenKind(TokenKind.IDENTIFIER);
            String name = currentToken().lexeme();
            Token lastToken = consume();

            var sourceInfo =  new SourceInfo(firstToken, lastToken);
            parameterList.add(new Parameter(type, name, sourceInfo));
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
            errors.add(new UnexpectedKindError(currentToken, kind));
            throw new FatalParsingException();
        }
    }

    private void expectAndIncrement(TokenKind kind) {
        Token currentToken = currentToken();
        if (currentToken.kind() != kind) {
            errors.add(new UnexpectedKindError(currentToken, kind));
        } else {
            increment();
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

    private Token consume() {
        var currentToken = currentToken();
        increment();
        return currentToken;
    }
}