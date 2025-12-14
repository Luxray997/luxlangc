package main.analysis.errors;

import main.lexer.objects.Token;
import main.parser.nodes.statements.Statement;

public record UnreachableStatementError(
    Token startToken,
    Token endToken
) implements AnalysisError {
    public UnreachableStatementError(Statement statement) {
        this(statement.sourceInfo().firstToken(), statement.sourceInfo().lastToken());
    }

    @Override
    public String reason() {
        return "Unreachable statement";
    }
}
