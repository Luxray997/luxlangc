package luxlang.compiler.analysis.nodes.expressions;

import luxlang.compiler.parser.objects.SourceInfo;
import luxlang.compiler.parser.nodes.Type;

import java.util.List;

public record AnalyzedFunctionCall(
    String name,
    List<AnalyzedExpression> arguments,
    Type resultType,
    SourceInfo sourceInfo
) implements AnalyzedExpression { }