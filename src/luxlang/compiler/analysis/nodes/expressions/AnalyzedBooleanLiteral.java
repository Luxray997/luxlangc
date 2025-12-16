package luxlang.compiler.analysis.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;

public record AnalyzedBooleanLiteral(
    boolean value,
    SourceInfo sourceInfo
) implements AnalyzedExpression {
    @Override
    public Type resultType() {
        return Type.BOOL;
    }
}