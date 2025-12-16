package luxlang.compiler.lexer.objects;

public record Token(
    TokenKind kind,
    String lexeme,
    int line,
    int column
) {
}