package luxlang.compiler.utils;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;

import java.util.ArrayList;
import java.util.List;

public class TokenListBuilder {
    private final List<Token> tokens = new ArrayList<>();
    private int line = 1;
    private int column = 1;

    private TokenListBuilder add(TokenKind kind, String lexeme) {
        tokens.add(new Token(kind, lexeme, line, column));
        column += lexeme.length();
        return this;
    }

    public TokenListBuilder identifier(String name) {
        return add(TokenKind.IDENTIFIER, name);
    }

    public TokenListBuilder integerLiteral(String lexeme) {
        return add(TokenKind.LITERAL_INTEGER, lexeme);
    }

    public TokenListBuilder floatingPointLiteral(String lexeme) {
        return add(TokenKind.LITERAL_FLOATINGPT, lexeme);
    }

    public TokenListBuilder typeVoid() {
        return add(TokenKind.VOID, "void");
    }

    public TokenListBuilder typeBool() {
        return add(TokenKind.BOOL, "bool");
    }

    public TokenListBuilder typeByte() {
        return add(TokenKind.BYTE, "byte");
    }

    public TokenListBuilder typeUbyte() {
        return add(TokenKind.UBYTE, "ubyte");
    }

    public TokenListBuilder typeShort() {
        return add(TokenKind.SHORT, "short");
    }

    public TokenListBuilder typeUshort() {
        return add(TokenKind.USHORT, "ushort");
    }

    public TokenListBuilder typeInt() {
        return add(TokenKind.INT, "int");
    }

    public TokenListBuilder typeUint() {
        return add(TokenKind.UINT, "uint");
    }

    public TokenListBuilder typeLong() {
        return add(TokenKind.LONG, "long");
    }

    public TokenListBuilder typeUlong() {
        return add(TokenKind.ULONG, "ulong");
    }

    public TokenListBuilder typeFloat() {
        return add(TokenKind.FLOAT, "float");
    }

    public TokenListBuilder typeDouble() {
        return add(TokenKind.DOUBLE, "double");
    }

    public TokenListBuilder keywordIf() {
        return add(TokenKind.IF, "if");
    }

    public TokenListBuilder keywordElse() {
        return add(TokenKind.ELSE, "else");
    }

    public TokenListBuilder keywordDo() {
        return add(TokenKind.DO, "do");
    }

    public TokenListBuilder keywordWhile() {
        return add(TokenKind.WHILE, "while");
    }

    public TokenListBuilder keywordFor() {
        return add(TokenKind.FOR, "for");
    }

    public TokenListBuilder keywordReturn() {
        return add(TokenKind.RETURN, "return");
    }

    public TokenListBuilder trueLiteral() {
        return add(TokenKind.TRUE, "true");
    }

    public TokenListBuilder falseLiteral() {
        return add(TokenKind.FALSE, "false");
    }

    public TokenListBuilder add() {
        return add(TokenKind.ADD, "+");
    }

    public TokenListBuilder sub() {
        return add(TokenKind.SUB, "-");
    }

    public TokenListBuilder mult() {
        return add(TokenKind.MULT, "*");
    }

    public TokenListBuilder div() {
        return add(TokenKind.DIV, "/");
    }

    public TokenListBuilder mod() {
        return add(TokenKind.MOD, "%");
    }

    public TokenListBuilder logicalAnd() {
        return add(TokenKind.LOGICAL_AND, "&&");
    }

    public TokenListBuilder logicalOr() {
        return add(TokenKind.LOGICAL_OR, "||");
    }

    public TokenListBuilder logicalNot() {
        return add(TokenKind.LOGICAL_NOT, "!");
    }

    public TokenListBuilder bitwiseAnd() {
        return add(TokenKind.BITWISE_AND, "&");
    }

    public TokenListBuilder bitwiseOr() {
        return add(TokenKind.BITWISE_OR, "|");
    }

    public TokenListBuilder bitwiseNot() {
        return add(TokenKind.BITWISE_NOT, "~");
    }

    public TokenListBuilder bitwiseXor() {
        return add(TokenKind.BITWISE_XOR, "^");
    }

    public TokenListBuilder equal() {
        return add(TokenKind.EQUAL, "==");
    }

    public TokenListBuilder notEqual() {
        return add(TokenKind.NOT_EQUAL, "!=");
    }

    public TokenListBuilder less() {
        return add(TokenKind.LESS, "<");
    }

    public TokenListBuilder lessEqual() {
        return add(TokenKind.LESS_EQUAL, "<=");
    }

    public TokenListBuilder greater() {
        return add(TokenKind.GREATER, ">");
    }

    public TokenListBuilder greaterEqual() {
        return add(TokenKind.GREATER_EQUAL, ">=");
    }

    public TokenListBuilder assign() {
        return add(TokenKind.ASSIGN, "=");
    }

    public TokenListBuilder leftParen() {
        return add(TokenKind.LEFT_PAREN, "(");
    }

    public TokenListBuilder rightParen() {
        return add(TokenKind.RIGHT_PAREN, ")");
    }

    public TokenListBuilder leftBrace() {
        return add(TokenKind.LEFT_BRACE, "{");
    }

    public TokenListBuilder rightBrace() {
        return add(TokenKind.RIGHT_BRACE, "}");
    }

    public TokenListBuilder semicolon() {
        return add(TokenKind.SEMICOLON, ";");
    }

    public TokenListBuilder comma() {
        return add(TokenKind.COMMA, ",");
    }

    public TokenListBuilder eof() {
        tokens.add(new Token(TokenKind.EOF, null, line, column));
        return this;
    }

    public TokenListBuilder newLine() {
        line++;
        column = 1;
        return this;
    }

    public TokenListBuilder whitespace(int amt) {
        column += amt;
        return this;
    }

    public TokenListBuilder tab() {
        return whitespace(4);
    }

    public TokenListBuilder space() {
        return whitespace(1);
    }

    public List<Token> build() {
        return tokens;
    }

    public static TokenListBuilder tokenListBuilder() {
        return new TokenListBuilder();
    }
}
