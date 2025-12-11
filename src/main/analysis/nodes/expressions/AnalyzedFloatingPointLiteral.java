package main.analysis.nodes.expressions;

import main.parser.SourceInfo;
import main.parser.nodes.Type;

public record AnalyzedFloatingPointLiteral(
    double value,
    Type type,
    SourceInfo sourceInfo
) implements AnalyzedExpression {
    @Override
    public Type resultType() {
        return type;
    }
}