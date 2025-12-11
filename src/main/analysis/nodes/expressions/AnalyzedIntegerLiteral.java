package main.analysis.nodes.expressions;

import main.parser.SourceInfo;
import main.parser.nodes.Type;

public record AnalyzedIntegerLiteral(
    long value,
    Type type,
    SourceInfo sourceInfo
) implements AnalyzedExpression {
    @Override
    public Type resultType() {
        return type;
    }
}