package luxlang.compiler.parser.errors;

import luxlang.compiler.lexer.objects.Token;

public record NotAStatementError(
    String reason,
    Token token
) implements ParsingError {
    private static final String REASON_TEMPLATE = "Not a statement";

    public NotAStatementError(Token token) {
        this(REASON_TEMPLATE, token);
    }
}
