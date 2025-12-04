package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;
import main.parser.nodes.Type;

import java.util.Optional;

public record AnalyzedVariableDeclaration(
    Type type,
    String name,
    Optional<AnalyzedExpression> initialValue
) implements AnalyzedStatement, AnalyzedForStatement.Initializer {
    @Override
    public boolean hasGuaranteedReturn() {
        return false;
    }
}