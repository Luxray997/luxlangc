package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.expressions.FunctionCall;

public record UndefinedFunctionError(
        String reason,
        Token startToken,
        Token endToken
) implements AnalysisError {
    private static final String REASON_TEMPLATE = "Function with name '%s' is not defined in scope";

    public UndefinedFunctionError(FunctionCall functionCall) {
        this(
            REASON_TEMPLATE.formatted(functionCall.name()),
            functionCall.sourceInfo().firstToken(),
            functionCall.sourceInfo().lastToken()
        );
    }

}
