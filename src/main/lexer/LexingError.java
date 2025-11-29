package main.lexer;

import main.errors.SourceCodeError;

public class LexingError extends SourceCodeError {
    public LexingError(int line, int column) {
        super("Error while lexing", line, column);
    }
}
