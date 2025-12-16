package luxlang.compiler.analysis.errors;

import luxlang.compiler.lexer.objects.Token;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.statements.Assignment;
import luxlang.compiler.parser.nodes.statements.VariableDeclaration;

public record TypeMismatchError(
        String reason,
        Token startToken,
        Token endToken
) implements AnalysisError {
    private static final String REASON_TEMPLATE = "Variable '%s' declared with type '%s' but assigned to value of type '%s'";

    public TypeMismatchError(Assignment assignment, Type variableType, Type valueType) {
        this(
            REASON_TEMPLATE.formatted(assignment.variableName(), variableType.lexeme(), valueType.lexeme()),
            assignment.sourceInfo().firstToken(),
            assignment.sourceInfo().lastToken()
        );
    }

    public TypeMismatchError(VariableDeclaration variableDeclaration, Type valueType) {
        this(
            REASON_TEMPLATE.formatted(variableDeclaration.name(), variableDeclaration.type().lexeme(), valueType.lexeme()),
            variableDeclaration.sourceInfo().firstToken(),
            variableDeclaration.sourceInfo().lastToken()
        );
    }
}
