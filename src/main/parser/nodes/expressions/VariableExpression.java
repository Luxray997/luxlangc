package main.parser.nodes.expressions;

import main.parser.SourceInfo;

public record VariableExpression(
    String name,
    SourceInfo sourceInfo
) implements Expression { }