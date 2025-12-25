package luxlang.compiler.analysis.warnings;

import luxlang.compiler.errors.SourceCodeWarning;
import luxlang.compiler.lexer.objects.Token;

public interface AnalysisWarning extends SourceCodeWarning {
    String reason();
    Token startToken();
    Token endToken();

    @Override
    default int line() {
        return startToken().line();
    }

    @Override
    default int column() {
        return startToken().column();
    }
}