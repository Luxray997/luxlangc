package main.errors;

import main.lexer.Token;
import main.parser.nodes.expressions.FunctionCall;
import main.parser.nodes.expressions.VariableExpression;

public record UndefinedFunctionError(
        String reason,
        Token startToken,
        Token endToken
) implements SrcCodeError {
    private static final String REASON_TEMPLATE = "Function with name '%s' is not defined in scope";

    public UndefinedFunctionError(FunctionCall functionCall) {
        this(
            REASON_TEMPLATE.formatted(functionCall.name()),
            functionCall.sourceInfo().firstToken(),
            functionCall.sourceInfo().lastToken()
        );
    }

}
