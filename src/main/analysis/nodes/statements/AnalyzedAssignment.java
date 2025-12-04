package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;

public record AnalyzedAssignment(
    String variableName,
    AnalyzedExpression value
) implements AnalyzedStatement, AnalyzedForStatement.Initializer {
    @Override
    public boolean hasGuaranteedReturn() {
        return false;
    }
}