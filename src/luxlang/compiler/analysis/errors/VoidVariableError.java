package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.Parameter;
import luxlang.compiler.parser.nodes.statements.VariableDeclaration;

public record VoidVariableError(
        String reason,
        Token startToken,
        Token endToken
) implements AnalysisError {
    private static final String REASON_TEMPLATE_VARIABLE = "Variable cannot be declared as void type";
    private static final String REASON_TEMPLATE_PARAMETER = "Parameter cannot be declared as void type";

    public VoidVariableError(Parameter parameter) {
        this(
            REASON_TEMPLATE_PARAMETER,
            parameter.sourceInfo().firstToken(),
            parameter.sourceInfo().lastToken()
        );
    }

    public VoidVariableError(VariableDeclaration variable) {
        this(
            REASON_TEMPLATE_VARIABLE,
            variable.sourceInfo().firstToken(),
            variable.sourceInfo().lastToken()
        );
    }

}
