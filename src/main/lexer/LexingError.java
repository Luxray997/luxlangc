package main.lexer;

public class LexingError extends RuntimeException {
    public LexingError(int line, int column) {
        super("Error while lexing source at line " + line + " and column " + column);
    }
}
