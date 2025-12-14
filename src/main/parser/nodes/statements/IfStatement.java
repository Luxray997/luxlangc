package main.parser.nodes.statements;

import main.parser.objects.SourceInfo;
import main.parser.nodes.expressions.Expression;

import java.util.Optional;

public record IfStatement(
    Expression condition,
    Statement body,
    Optional<Statement> elseBody,
    SourceInfo sourceInfo
) implements Statement { }