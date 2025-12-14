package main.parser.nodes.statements;

import main.parser.objects.SourceInfo;
import main.parser.nodes.expressions.Expression;

public record DoWhileStatement(
    Statement body,
    Expression condition,
    SourceInfo sourceInfo
) implements Statement { }