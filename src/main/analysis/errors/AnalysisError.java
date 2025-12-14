package main.analysis.errors;

import main.errors.SourceCodeError;
import main.lexer.objects.Token;

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
