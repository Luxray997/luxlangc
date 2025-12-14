package main.parser.errors;

import main.lexer.objects.Token;

public record NotAStatementError(
    String reason,
    Token token
) implements ParsingError {
    private static final String REASON_TEMPLATE = "Not a statement";

    public NotAStatementError(Token token) {
        this(REASON_TEMPLATE, token);
    }
}
