package main.errors;

import main.lexer.Token;
import main.parser.nodes.Type;
import main.parser.nodes.statements.ReturnStatement;

import static main.parser.nodes.Type.VOID;

public record ReturnTypeError(
        String reason,
        Token startToken,
        Token endToken
) implements SrcCodeError {
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
