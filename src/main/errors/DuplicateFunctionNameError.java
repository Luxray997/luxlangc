package main.errors;

import main.lexer.Token;
import main.parser.nodes.FunctionDeclaration;

public record DuplicateFunctionNameError(
    String reason,
    Token startToken,
    Token endToken
) implements SrcCodeError {
    private static final String REASON_TEMPLATE = "Function with name '%s' conflicts with another function in scope";

    public DuplicateFunctionNameError(FunctionDeclaration function) {
        this(REASON_TEMPLATE.formatted(function.name()), function.sourceInfo().firstToken(), function.sourceInfo().lastToken());
    }
}
