package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.FunctionDeclaration;

public record IndeterminateReturnError(
    Token startToken,
    Token endToken
) implements AnalysisError {

    public IndeterminateReturnError(FunctionDeclaration functionDeclaration) {
        this(functionDeclaration.sourceInfo().firstToken(), functionDeclaration.sourceInfo().lastToken());
    }

    @Override
    public String reason() {
        return "Not all code paths return a value";
    }
}
