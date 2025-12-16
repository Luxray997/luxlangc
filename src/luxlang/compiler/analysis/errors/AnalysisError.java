package luxlang.compiler.analysis.errors;

import luxlang.compiler.errors.SourceCodeError;
import luxlang.compiler.lexer.objects.Token;

public interface AnalysisError extends SourceCodeError {
    String reason();
    Token startToken();
    Token endToken();

    @Override
    default int  line() {
        return startToken().line();
    }

    @Override
    default int column() {
        return  startToken().column();
    }
}
