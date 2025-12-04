package main.analysis.nodes.expressions;

import main.parser.nodes.Type;

public record AnalyzedBooleanLiteral(boolean value) implements AnalyzedExpression {
    @Override
    public Type resultType() {
        return Type.BOOL;
    }
}