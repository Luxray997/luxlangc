package main.analysis.errors;

import main.lexer.objects.Token;
import main.parser.nodes.Type;
import main.parser.nodes.statements.ReturnStatement;

public record ReturnTypeError(
        String reason,
        Token startToken,
        Token endToken
) implements AnalysisError {
    private static final String REASON_TEMPLATE_VOID = "Returned a value in void function";
    private static final String REASON_TEMPLATE_MISMATCH = "Returned value of type '%s' in function with return type of '%s'";

    public ReturnTypeError(ReturnStatement returnStatement, Type expectedType, Type valueType) {
        this(
            REASON_TEMPLATE_MISMATCH.formatted(valueType.lexeme(), expectedType.lexeme()),
            returnStatement.sourceInfo().firstToken(),
            returnStatement.sourceInfo().lastToken()
        );
    }

    public ReturnTypeError(ReturnStatement returnStatement) {
        this(
            REASON_TEMPLATE_VOID,
            returnStatement.sourceInfo().firstToken(),
            returnStatement.sourceInfo().lastToken()
        );
    }

}
