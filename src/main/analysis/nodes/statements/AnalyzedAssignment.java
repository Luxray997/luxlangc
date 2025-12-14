package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;
import main.parser.objects.SourceInfo;

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