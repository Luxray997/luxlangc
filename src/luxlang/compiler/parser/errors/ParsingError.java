package luxlang.compiler.parser.errors;

import luxlang.compiler.errors.SourceCodeError;
import luxlang.compiler.lexer.objects.Token;

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