package main.parser.nodes.expressions;

import java.util.List;

public record FunctionCall(
    String name,
    List<Expression> arguments
) implements Expression { }