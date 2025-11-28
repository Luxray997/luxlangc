package main.lexer;

public record Token(
    TokenKind kind,
    String lexeme,
    int line,
    int column
) {
}