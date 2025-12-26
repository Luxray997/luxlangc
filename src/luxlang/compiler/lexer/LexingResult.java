package luxlang.compiler.lexer;

import luxlang.compiler.lexer.errors.LexingError;
import luxlang.compiler.lexer.objects.Token;

import java.util.List;

public sealed interface LexingResult permits LexingResult.Success, LexingResult.Failure {
    record Success(List<Token> tokens) implements LexingResult { }
    record Failure(List<LexingError> errors) implements LexingResult { }

    static LexingResult success(List<Token> tokens) {
        return new LexingResult.Success(tokens);
    }

    static LexingResult failure(List<LexingError> errors) {
        return new LexingResult.Failure(errors);
    }
}