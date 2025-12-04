package main.analysis.nodes.expressions;

import main.parser.nodes.Type;

public record AnalyzedVariableExpression(
    String name,
    Type resultType
) implements AnalyzedExpression { }