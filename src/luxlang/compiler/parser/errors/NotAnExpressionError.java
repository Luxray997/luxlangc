package luxlang.compiler.parser.errors;

import luxlang.compiler.lexer.objects.Token;

public record NotAnExpressionError(
    String reason,
    Token token
) implements ParsingError {
    private static final String REASON_TEMPLATE = "Not an expression";

    public NotAnExpressionError(Token token) {
        this(REASON_TEMPLATE, token);
    }
}
