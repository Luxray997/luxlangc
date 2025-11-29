package main.errors;

import main.lexer.Token;

public class SourceCodeError extends RuntimeException {
    public SourceCodeError(String message, Token token) {
        super(message + " (line " + token.line() + ", column " + token.column() + ")");

    }

    public SourceCodeError(String message, int line, int column) {
        super(message + "(line " + line + ", column " + column + ")");
    }
}