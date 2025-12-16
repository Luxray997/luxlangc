package luxlang.compiler.parser;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.lexer.objects.TokenKind;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building Token lists in parser tests.
 * Provides a fluent API for creating token sequences.
 */
public class ParserTestUtils {
    
    public static class TokenBuilder {
        private final List<Token> tokens = new ArrayList<>();
        private int line = 1;
        private int column = 1;
        
        public TokenBuilder type(TokenKind kind, String lexeme) {
            tokens.add(new Token(kind, lexeme, line, column));
            column += lexeme.length();
            return this;
        }
        
        public TokenBuilder keyword(TokenKind kind) {
            String lexeme = kind.lexeme() != null ? kind.lexeme() : "";
            return type(kind, lexeme);
        }
        
        public TokenBuilder identifier(String name) {
            return type(TokenKind.IDENTIFIER, name);
        }
        
        public TokenBuilder intLiteral(String value) {
            return type(TokenKind.LITERAL_INTEGER, value);
        }
        
        public TokenBuilder operator(TokenKind kind) {
            String lexeme = kind.lexeme() != null ? kind.lexeme() : "";
            return type(kind, lexeme);
        }
        
        public TokenBuilder punctuation(TokenKind kind) {
            String lexeme = kind.lexeme() != null ? kind.lexeme() : "";
            return type(kind, lexeme);
        }
        
        public TokenBuilder newLine() {
            line++;
            column = 1;
            return this;
        }
        
        public TokenBuilder eof() {
            tokens.add(new Token(TokenKind.EOF, "", line, column));
            return this;
        }
        
        public List<Token> build() {
            return tokens;
        }
    }
    
    public static TokenBuilder tokens() {
        return new TokenBuilder();
    }
}
