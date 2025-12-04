package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;

import java.util.Optional;

public record AnalyzedReturnStatement(Optional<AnalyzedExpression> value) implements AnalyzedStatement {
    public boolean hasGuaranteedReturn() {
        return true;
    }
}