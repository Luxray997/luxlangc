package main.parser.errors;

import main.lexer.Token;
import main.lexer.TokenKind;

public class UnexpectedKindError extends ParsingError {
    public UnexpectedKindError(Token token, TokenKind expectedKind) {
        super("Expected token of type '" + expectedKind + "', instead got '" + token.kind() + "'", token);
    }
}