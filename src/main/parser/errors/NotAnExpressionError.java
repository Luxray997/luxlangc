package main.parser.errors;

import main.lexer.objects.Token;

public record NotAnExpressionError(
    String reason,
    Token token
) implements ParsingError {
    private static final String REASON_TEMPLATE = "Not an expression";

    public NotAnExpressionError(Token token) {
        this(REASON_TEMPLATE, token);
    }
}
