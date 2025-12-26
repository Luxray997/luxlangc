package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.statements.ReturnStatement;

public record ReturnMissingValueError(
        String reason,
        Token startToken,
        Token endToken
) implements AnalysisError {
    private static final String REASON_TEMPLATE = "Return value expected";

    public ReturnMissingValueError(ReturnStatement returnStatement) {
        this(
            REASON_TEMPLATE,
            returnStatement.sourceInfo().firstToken(),
            returnStatement.sourceInfo().lastToken()
        );
    }

}
