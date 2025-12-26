package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.expressions.VariableExpression;
import luxlang.compiler.parser.nodes.statements.Assignment;

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
