package luxlang.compiler.parser.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;

public record UnexpectedKindError(
    String reason,
    Token token
) implements ParsingError {
    private static final String REASON_TEMPLATE = "Expected '%s', instead got '%s'";

    public UnexpectedKindError(Token token, TokenKind expectedKind) {
        this(REASON_TEMPLATE.formatted(expectedKind.lexeme(), token.lexeme()), token);
    }
}