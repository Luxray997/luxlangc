package main.parser.nodes.statements;

import main.parser.nodes.expressions.Expression;

public record WhileStatement(
    Expression condition,
    Statement body
) implements Statement { }