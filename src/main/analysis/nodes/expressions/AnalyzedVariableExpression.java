package main.analysis.nodes.expressions;

import main.parser.SourceInfo;
import main.parser.nodes.Type;

public record AnalyzedVariableExpression(
    String name,
    Type resultType,
    SourceInfo sourceInfo
) implements AnalyzedExpression { }