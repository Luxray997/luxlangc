package main.errors;

import main.lexer.Token;
import main.parser.nodes.FunctionDeclaration;

public record IndeterminateReturnError(
    Token startToken,
    Token endToken
) implements SrcCodeError {

    public IndeterminateReturnError(FunctionDeclaration functionDeclaration) {
        this(functionDeclaration.sourceInfo().firstToken(), functionDeclaration.sourceInfo().lastToken());
    }

    @Override
    public String reason() {
        return "Not all code paths return a value";
    }
}
