package luxlang.compiler.parser.errors;

import luxlang.compiler.lexer.objects.Token;

public record NotATypeError(
    String reason,
    Token token
) implements ParsingError {
    private static final String REASON_TEMPLATE = "Expected a type, instead got '%s'";

    public NotATypeError(Token token) {
        this(REASON_TEMPLATE.formatted(token.lexeme()), token);
    }
}
