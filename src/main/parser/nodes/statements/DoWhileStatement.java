package main.parser.nodes.statements;

import main.parser.nodes.expressions.Expression;

public record DoWhileStatement(
    Statement body,
    Expression condition
) implements Statement { }