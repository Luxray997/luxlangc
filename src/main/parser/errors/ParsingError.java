package main.parser.errors;

import main.errors.SourceCodeError;
import main.lexer.Token;

public class ParsingError extends SourceCodeError {
    public ParsingError(String message, Token token) {
        super(message, token);
    }
}