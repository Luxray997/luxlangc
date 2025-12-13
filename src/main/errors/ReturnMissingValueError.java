package main.errors;

import main.lexer.Token;
import main.parser.nodes.statements.ReturnStatement;

public record ReturnMissingValueError(
        String reason,
        Token startToken,
        Token endToken
) implements SrcCodeError {
    private static final String REASON_TEMPLATE = "Return value expected";

    public ReturnMissingValueError(ReturnStatement returnStatement) {
        this(
            REASON_TEMPLATE,
            returnStatement.sourceInfo().firstToken(),
            returnStatement.sourceInfo().lastToken()
        );
    }

}
