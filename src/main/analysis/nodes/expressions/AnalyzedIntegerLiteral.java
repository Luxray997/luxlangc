package main.analysis.nodes.expressions;

import main.parser.nodes.Type;

public record AnalyzedIntegerLiteral(
    long value,
    Type type
) implements AnalyzedExpression {
    @Override
    public Type resultType() {
        return type;
    }
}