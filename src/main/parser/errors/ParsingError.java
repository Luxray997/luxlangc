package main.parser.errors;

import main.errors.SourceCodeError;
import main.lexer.objects.Token;

public interface ParsingError extends SourceCodeError {
    String reason();
    Token token();

    @Override
    default int line() {
        return token().line();
    }

    @Override
    default int column() {
        return token().column();
    }
}