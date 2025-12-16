package luxlang.compiler.analysis.nodes.statements;

import luxlang.compiler.analysis.nodes.expressions.AnalyzedExpression;
import luxlang.compiler.parser.objects.SourceInfo;

public record AnalyzedAssignment(
    String variableName,
    AnalyzedExpression value,
    SourceInfo sourceInfo
) implements AnalyzedStatement, AnalyzedForStatement.Initializer {
    @Override
    public boolean hasGuaranteedReturn() {
        return false;
    }
}