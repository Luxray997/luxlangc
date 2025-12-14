package main.parser.nodes.statements;

import main.parser.objects.SourceInfo;
import main.parser.nodes.expressions.Expression;

public record Assignment(
    String variableName,
    Expression value,
    SourceInfo sourceInfo
) implements Statement, ForStatement.Initializer { }