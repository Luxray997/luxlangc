package main.analysis.errors;

import main.lexer.objects.Token;
import main.parser.nodes.expressions.VariableExpression;
import main.parser.nodes.statements.Assignment;

public record UndefinedVariableError(
        String reason,
        Token startToken,
        Token endToken
) implements AnalysisError {
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
