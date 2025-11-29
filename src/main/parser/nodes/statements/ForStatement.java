package main.parser.nodes.statements;

import main.parser.nodes.expressions.Expression;

import java.util.Optional;

public record ForStatement(
    Optional<Initializer> initializer,
    Optional<Expression> condition,
    Optional<Assignment> update,
    Statement body
) implements Statement {
    public sealed interface Initializer permits VariableDeclaration, Assignment { }
}