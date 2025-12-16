package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.statements.Statement;

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
