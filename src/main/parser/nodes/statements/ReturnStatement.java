package main.parser.nodes.statements;

import main.parser.SourceInfo;
import main.parser.nodes.expressions.Expression;

import java.util.Optional;

public record ReturnStatement(
    Optional<Expression> value,
    SourceInfo sourceInfo
) implements Statement { }