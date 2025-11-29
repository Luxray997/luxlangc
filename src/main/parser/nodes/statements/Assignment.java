package main.parser.nodes.statements;

import main.parser.nodes.expressions.Expression;

public record Assignment(
    String variableName,
    Expression value
) implements Statement, ForStatement.Initializer { }