package main.parser.errors;

import main.lexer.objects.Token;
import main.lexer.objects.TokenKind;

public record UnexpectedKindError(
    String reason,
    Token token
) implements ParsingError {
    private static final String REASON_TEMPLATE = "Expected '%s', instead got '%s'";

    public UnexpectedKindError(Token token, TokenKind expectedKind) {
        this(REASON_TEMPLATE.formatted(expectedKind.lexeme(), token.lexeme()), token);
    }
}