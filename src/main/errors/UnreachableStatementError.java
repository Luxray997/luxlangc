package main.errors;

import main.lexer.Token;
import main.parser.nodes.statements.Statement;

public record UnreachableStatementError(
    Token startToken,
    Token endToken
) implements SrcCodeError {
    public UnreachableStatementError(Statement statement) {
        this(statement.sourceInfo().firstToken(), statement.sourceInfo().lastToken());
    }

    @Override
    public String reason() {
        return "Unreachable statement";
    }
}
