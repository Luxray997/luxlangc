package main.parser.nodes.statements;

import main.parser.nodes.Type;
import main.parser.nodes.expressions.Expression;

import java.util.Optional;

public record VariableDeclaration(
    Type type,
    String name,
    Optional<Expression> initialValue
) implements Statement, ForStatement.Initializer { }