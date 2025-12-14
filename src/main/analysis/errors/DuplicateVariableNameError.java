package main.analysis.errors;

import main.lexer.objects.Token;
import main.parser.nodes.Parameter;
import main.parser.nodes.statements.VariableDeclaration;

public record DuplicateVariableNameError(
        String reason,
        Token startToken,
        Token endToken
) implements AnalysisError {
    private static final String REASON_TEMPLATE_VARIABLE = "Variable with name '%s' conflicts with another variable in scope";
    private static final String REASON_TEMPLATE_PARAMETER = "Variable with name '%s' conflicts with another variable in scope";

    public DuplicateVariableNameError(Parameter parameter) {
        this(
            REASON_TEMPLATE_PARAMETER.formatted(parameter.name()),
            parameter.sourceInfo().firstToken(),
            parameter.sourceInfo().lastToken()
        );
    }

    public DuplicateVariableNameError(VariableDeclaration variable) {
        this(
            REASON_TEMPLATE_VARIABLE.formatted(variable.name()),
            variable.sourceInfo().firstToken(),
            variable.sourceInfo().lastToken()
        );
    }

}
