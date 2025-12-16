package luxlang.compiler.analysis.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;

public record AnalyzedVariableExpression(
    String name,
    Type resultType,
    SourceInfo sourceInfo
) implements AnalyzedExpression { }