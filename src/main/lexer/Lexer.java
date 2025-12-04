package main.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Lexer {
    private static final Pattern NUMBER_LITERAL_PATTERN = Pattern.compile(
        "^(?:\\d*\\.\\d+[dDfF]?|\\d+(?:[dDfF]|[uU][lLsSbB]?|[lLsSbB])?)"
    );

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

            if (Character.isDigit(currChar) || currChar == '.') {
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
        final int startLine = line;
        final int startColumn = column;
        final int startIndex = i;

        var matcher = NUMBER_LITERAL_PATTERN.matcher(source);
        matcher.region(startIndex, source.length());
        if (!matcher.lookingAt()) {
            throw new IllegalStateException("Number literal regex pattern didn't match number literal");
        }

        String lexeme = source.substring(startIndex, matcher.end());

        int literalLength = lexeme.length();
        for (int j = 0; j < literalLength; j++) {
            increment();
        }

        boolean isFloat = lexeme.contains(".") ||
            switch (lexeme.charAt(literalLength - 1)) {
                case 'D', 'd', 'F', 'f' -> true;
                default -> false;
            };

        TokenKind kind = isFloat ? TokenKind.LITERAL_FLOATINGPT : TokenKind.LITERAL_INTEGER;

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
        char previous = peekCurrentOrNull();
        i++;
        if (previous == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
    }
}