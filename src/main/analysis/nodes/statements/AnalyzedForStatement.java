package main.analysis.nodes.statements;

import main.analysis.nodes.expressions.AnalyzedExpression;

import java.util.Optional;

public record AnalyzedForStatement(
    Optional<Initializer> initializer,
    Optional<AnalyzedExpression> condition,
    Optional<AnalyzedAssignment> update,
    AnalyzedStatement body,
    boolean hasGuaranteedReturn
) implements AnalyzedStatement {
    public sealed interface Initializer permits AnalyzedVariableDeclaration, AnalyzedAssignment { }
}