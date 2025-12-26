package luxlang.compiler.analysis.nodes.statements;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedExpression;
import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;
import luxlang.compiler.parser.nodes.statements.VariableDeclaration;

import java.util.Optional;

public record AnalyzedVariableDeclaration(
    Type type,
    String name,
    Optional<AnalyzedExpression> initialValue,
    SourceInfo sourceInfo
) implements AnalyzedStatement, AnalyzedForStatement.Initializer {
    @Override
    public boolean hasGuaranteedReturn() {
        return false;
    }

    public static AnalyzedVariableDeclaration from(VariableDeclaration variableDeclaration, Optional<AnalyzedExpression> initialValue) {
        return new AnalyzedVariableDeclaration(variableDeclaration.type(), variableDeclaration.name(), initialValue, variableDeclaration.sourceInfo());
    }
}