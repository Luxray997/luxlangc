package main.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens;

    private int i;
    private int line;
    private int column;

    public Lexer(String source) {
        this.source = source;
        this.i = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
    }

    public List<Token> lex() {
        while (true) {
            char currChar = peekCurrentOrNull();
            if (currChar == '\0') {
                tokens.add(new Token(TokenKind.EOF, null, line, column));
                break;
            }

            if (Character.isWhitespace(currChar)) {
                increment();
                continue;
            }

            if (Character.isLetter(currChar)) {
                addKeywordOrIdentifier();
                continue;
            }

            if (Character.isDigit(currChar)) {
                addNumberLiteral();
                continue;
            }

            addOperatorOrPunctuation(currChar);
            increment();
        }

        return tokens;
    }

    private void addOperatorOrPunctuation(char currChar) {
        final int startLine = line;
        final int startColumn = column;
        char charAfterCurr = peekNextOrNull();
        TokenKind kind = TokenKind.getOperatorOrPunctuation(currChar, charAfterCurr);
        if (kind == TokenKind.ERROR) {
            throw new LexingError(line, column);
        }
        String lexeme = kind.getLexeme();
        if (lexeme.length() == 2) {
            increment();
        }
        tokens.add(new Token(kind, lexeme, startLine, startColumn));
    }

    private void addNumberLiteral() {
        char currChar;
        final int startLine = line;
        final int startColumn = column;
        final int startIndex = i;
        do {
            increment();
            currChar = peekCurrentOrNull();
        } while (Character.isDigit(currChar) || currChar == '.');
        String lexeme = source.substring(startIndex, i);
        TokenKind kind = lexeme.contains(".") ? TokenKind.LITERAL_FLOATINGPT : TokenKind.LITERAL_INTEGER;
        tokens.add(new Token(kind, lexeme, startLine, startColumn));
    }

    private void addKeywordOrIdentifier() {
        final int startLine = line;
        final int startColumn = column;
        final int startIndex = i;
        char currChar;
        do {
            increment();
            currChar = peekCurrentOrNull();
        } while (Character.isLetterOrDigit(currChar) || currChar == '_');
        String lexeme = source.substring(startIndex, i);
        TokenKind kind = TokenKind.getKeywordOrIdentifier(lexeme);
        tokens.add(new Token(kind, lexeme, startLine, startColumn));
    }

    private char peekCurrentOrNull() {
        if (i >= source.length()) {
            return '\0';
        }
        return source.charAt(i);
    }

    private char peekNextOrNull() {
        if (i + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(i + 1);
    }

    private void increment() {
        char next = peekCurrentOrNull();
        i++;
        if (next == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
    }
}