package luxlang.compiler.lexer.errors;

public record UnexpectedTokenError(
    String reason,
    int line,
    int column
) implements LexingError {
    private static final String REASON_TEMPLATE = "Unexpected token";

    public UnexpectedTokenError(int line, int column) {
        this(REASON_TEMPLATE, line, column);
    }
}
