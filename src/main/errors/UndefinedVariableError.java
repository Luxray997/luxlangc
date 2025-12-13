package main.errors;

import main.lexer.Token;
import main.parser.nodes.Parameter;
import main.parser.nodes.expressions.VariableExpression;
import main.parser.nodes.statements.Assignment;
import main.parser.nodes.statements.VariableDeclaration;

public record UndefinedVariableError(
        String reason,
        Token startToken,
        Token endToken
) implements SrcCodeError {
    private static final String REASON_TEMPLATE = "Variable with name '%s' is not defined in scope";

    public UndefinedVariableError(VariableExpression variable) {
        this(
            REASON_TEMPLATE.formatted(variable.name()),
            variable.sourceInfo().firstToken(),
            variable.sourceInfo().lastToken()
        );
    }

    public UndefinedVariableError(Assignment assignment) {
        this(
            REASON_TEMPLATE.formatted(assignment.variableName()),
            assignment.sourceInfo().firstToken(),
            assignment.sourceInfo().lastToken()
        );
    }

}
