package main.parser.nodes.expressions;

import main.parser.objects.SourceInfo;

import java.util.List;

public record FunctionCall(
    String name,
    List<Expression> arguments,
    SourceInfo sourceInfo
) implements Expression { }