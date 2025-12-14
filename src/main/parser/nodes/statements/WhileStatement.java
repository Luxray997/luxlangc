package main.parser.nodes.statements;

import main.parser.objects.SourceInfo;
import main.parser.nodes.expressions.Expression;

public record WhileStatement(
    Expression condition,
    Statement body,
    SourceInfo sourceInfo
) implements Statement { }