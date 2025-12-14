package main.analysis.nodes.expressions;

import main.parser.objects.SourceInfo;
import main.parser.nodes.Type;

import java.util.List;

public record AnalyzedFunctionCall(
    String name,
    List<AnalyzedExpression> arguments,
    Type resultType,
    SourceInfo sourceInfo
) implements AnalyzedExpression { }