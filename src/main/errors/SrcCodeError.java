package main.errors;

import main.lexer.Token;

// TODO: rename to SourceCodeError after removing the old class
public interface SrcCodeError {
    String reason();
    Token startToken();
    Token endToken();
}
