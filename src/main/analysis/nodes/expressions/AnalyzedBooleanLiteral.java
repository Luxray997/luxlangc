package main.analysis.nodes.expressions;

import main.parser.objects.SourceInfo;
import main.parser.nodes.Type;

public record AnalyzedBooleanLiteral(
    boolean value,
    SourceInfo sourceInfo
) implements AnalyzedExpression {
    @Override
    public Type resultType() {
        return Type.BOOL;
    }
}